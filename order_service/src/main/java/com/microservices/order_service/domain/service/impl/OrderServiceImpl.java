package com.microservices.order_service.domain.service.impl;

import com.microservices.common_service.constants.ResponseConstants;
import com.microservices.common_service.domain.PageRequest;
import com.microservices.common_service.domain.PageResponse;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.exception.ApiException;
import com.microservices.common_service.utils.CommonUtils;
import com.microservices.order_service.application.request.OrderItemRequest;
import com.microservices.order_service.application.request.OrderRequest;
import com.microservices.order_service.application.respone.OrderDetailResponse;
import com.microservices.order_service.application.respone.OrderItemResponse;
import com.microservices.order_service.application.respone.OrderResponse;
import com.microservices.order_service.application.respone.SalesByProvinceResponse;
import com.microservices.order_service.domain.constant.Constants.OrderStatusEnum;
import com.microservices.order_service.domain.db_repo.OrderDomainRepo;
import com.microservices.order_service.domain.mapper.OrderItemMapper;
import com.microservices.order_service.domain.mapper.OrderMapper;
import com.microservices.order_service.domain.model.OrderItemModel;
import com.microservices.order_service.domain.model.OrderModel;
import com.microservices.order_service.domain.model.TrackModel;
import com.microservices.order_service.domain.outbound.feingclient.AddressFeignClient;
import com.microservices.order_service.domain.outbound.feingclient.InventoryFeignClient;
import com.microservices.order_service.domain.outbound.feingclient.ProductFeignClient;
import com.microservices.order_service.domain.outbound.request.InventoryRequest;
import com.microservices.order_service.domain.outbound.respone.AddressResponse;
import com.microservices.order_service.domain.outbound.respone.InventoryResponse;
import com.microservices.order_service.domain.outbound.respone.ProductResponse;
import com.microservices.order_service.domain.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderDomainRepo orderDomainRepo;
    private final OrderItemMapper itemMapper;
    private final OrderMapper orderMapper;
    private final ProductFeignClient productFeignClient;
    private final InventoryFeignClient inventoryFeignClient;
    private final AddressFeignClient addressFeignClient;


    @Override
    public ResponseModel<PageResponse<OrderResponse>> listAllOrders(PageRequest request) {
        log.info("Start: Getting all orders :{} ", CommonUtils.toJsonString(request));

        // 1. Fetch Orders from Database
        Page<OrderModel> orderModels = orderDomainRepo.listAllOrders(request.toPageable());

        // 2. Extract unique Address IDs
        List<UUID> addressIds = orderModels.stream()
                .map(OrderModel::getDeliveryAddressId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // 3. Fetch all Address details
        Map<UUID, AddressResponse> addressMap = fetchAddressMap(addressIds);

        // 4. Map and Enrich
        List<OrderResponse> orderResponse = orderModels.stream().map(model -> {
            // Map order fields
            OrderResponse res = orderMapper.toOrderResponse(model);

            // Find the matching address from  map
            AddressResponse addr = addressMap.get(model.getDeliveryAddressId());
            if (addr != null) {
                res.setDeliveryAddressSummary(addr.getProvinceNameKh() + ", " + addr.getCountry());
            } else {
                log.error("Address is not found");
                res.setDeliveryAddressSummary("N/A");
            }

            return res;
        }).toList();

        // 5. Wrap in PageResponse
        PageResponse<OrderResponse> resData = PageResponse.fromPage(orderModels, orderResponse);

        log.info("Finish: Getting all orders with response: {}", CommonUtils.toJsonString(resData));
        return ResponseModel.success(resData);
    }

    @Override
    public ResponseModel<OrderDetailResponse> getOrderById(UUID orderId) {
        log.info("Start: getting order details for ID: {}", orderId);

        // 1. Fetch Order from Database
        OrderModel orderModel = orderDomainRepo.getOneOrder(orderId).orElseThrow(() -> {
            log.error("Order not found with orderId: {}", orderId);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Order is not found");
        });

        // 2. Map basic fields to the OrderDetailResponse
        OrderDetailResponse resData = orderMapper.toOrderDetailResponse(orderModel);

        // 3. Fetch the full Address object using your private helper
        if (orderModel.getDeliveryAddressId() != null) {
            Map<UUID, AddressResponse> addressMap = fetchAddressMap(List.of(orderModel.getDeliveryAddressId()));

            // 4. Set the full nested object
            resData.setDeliveryAddress(addressMap.get(orderModel.getDeliveryAddressId()));
        }

        log.info("Finish: get order detail with enriched address: {}", CommonUtils.toJsonString(resData));
        return ResponseModel.success(resData);
    }

    @Override
    public ResponseModel<List<OrderItemResponse>> listItemById(UUID orderId) {
        log.info("Start: Fetching items for Order ID: {}", orderId);

        // 1. Validate order id
        OrderModel orderModel = orderDomainRepo.getOneOrder(orderId).orElseThrow(() -> {
            log.error("Order id : {} is not found!", orderId);
            throw new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Order id: {} not found!", orderId);
        });

        // 2. Fetch all raw items from the database
        List<OrderItemModel> items = orderDomainRepo.findAllByOrderId(orderId);

        // Validate for both null and empty list
        if (items == null || items.isEmpty()) {
            log.error("Order items not found for orderId: {}", orderId);
            throw new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Order items not found!");
        }

        // 3. Filter only ACTIVE items
        List<OrderItemModel> activeItems = items.stream().filter(item -> "ACTIVE".equalsIgnoreCase(item.getStatus())).toList();

        // 4. Enrich items with data from Product Service (Feign Call)
        if (!activeItems.isEmpty()) {
            List<String> productIds = activeItems.stream().map(item -> item.getProductId().toString()).distinct().toList();

            try {
                log.info("Getting {} products from Product Service", productIds.size());
                ResponseModel<List<ProductResponse>> productResponse = productFeignClient.getProductByIds(productIds);

                if (productResponse != null && productResponse.getData() != null) {
                    Map<String, ProductResponse> responseMap = productResponse.getData().stream().collect(Collectors.toMap(ProductResponse::getProductId, prod -> prod, (a, b) -> a));

                    // Populate the display fields in the model
                    activeItems.forEach(item -> {
                        ProductResponse prodResp = responseMap.get(item.getProductId().toString());
                        if (prodResp != null) {
                            item.setProductName(prodResp.getProductName());
                            item.setDescription(prodResp.getDescription());
                            item.setUnitPrice(prodResp.getSalePrice());
                            item.setProductImage(prodResp.getImagePath());
                        }
                    });
                }
            } catch (Exception e) {
                // Log but continue—this makes the service resilient
                log.error("CRITICAL: Product Service enrichment failed: {}", e.getMessage());
            }
        }

        // 5. Map to order item response
        OrderItemResponse response = itemMapper.toOrderItemResponse(activeItems, orderModel.getUuid());

        log.info("Finish: Successfully retrieved {} items for order {}", activeItems.size(), orderId);

        // Wrap the object in a single-element list to match return type List<OrderItemResponse>
        return ResponseModel.success(List.of(response));
    }

    @Override
    @Transactional
    public ResponseModel<OrderResponse> createOrder(OrderRequest request) {
        log.info("Start: making order with body: {}", CommonUtils.toJsonString(request));

        // Validation: Check for empty items
        if (request.getItems() == null || request.getItems().isEmpty()) {
            log.error("Items list cannot be empty");
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "Items list cannot be empty");
        }

        // Map to get list product uuid
        List<String> productIds = request.getItems().stream().map(OrderItemRequest.OrderItem::getProductId).map(UUID::toString).toList();

        // Call product client & Prepare data
        ResponseModel<List<ProductResponse>> products = productFeignClient.getProductByIds(productIds);

        // Call inventory client & Prepare data
        ResponseModel<List<InventoryResponse>> fetchStock = inventoryFeignClient.getBulkStock(productIds);

        // Call method address client & Prepare data
        Map<UUID, AddressResponse> addressCheck = fetchAddressMap(List.of(request.getDeliveryAddressId()));

        // Map to price of productId
        Map<UUID, BigDecimal> priceMap = products.getData().stream().collect(Collectors.toMap(p -> UUID.fromString(p.getProductId()), ProductResponse::getSalePrice, (a, b) -> a));

        // Map to get stock
        Map<UUID, Integer> stockMap = fetchStock.getData().stream().collect(Collectors.toMap(InventoryResponse::getProductId, InventoryResponse::getAvailableStock, (a, b) -> a));

        // Validate  address Service
        if (addressCheck == null || addressCheck.isEmpty()) {
            log.error("Invalid Delivery Address");
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "Invalid Delivery Address");
        }

        // Fetch available stock from Inventory Service
        if (products.getData() == null) {
            log.error("Failed to fetch product details from Product Service");
            throw new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Failed to fetch product details");
        }

        // Fetch available stock from Inventory Service
        if (fetchStock.getData() == null) {
            log.error("Failed to fetch inventory details from inventory Service");
            throw new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Failed to get product details");
        }

        // Validation: Check stock fetched
        if (stockMap.isEmpty()) {
            log.error("Failed to fetch stock details from Inventory Service");
            throw new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Failed to fetch stock details");
        }

        // Validate total request size (Size check)
        if (request.getItems().size() > priceMap.size()) {
            log.error("Failed: Some products are invalid or unavailable");
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "Some products are invalid or unavailable.");
        }

        // Declaration of grand total and items to save
        BigDecimal grandTotal = BigDecimal.ZERO;
        List<OrderItemModel> itemsToSave = new ArrayList<>();

        for (OrderItemRequest.OrderItem itemDto : request.getItems()) {
            UUID pId = itemDto.getProductId();
            BigDecimal unitPrice = priceMap.get(pId);
            Integer availableStock = stockMap.getOrDefault(pId, 0);

            // Validation price
            if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                log.error("Price not found for product Id: {}", itemDto.getProductId());
                throw new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Price not found for id : " + itemDto.getProductId());
            }

            // 2. Stock Validation (Combined and fixed null-safety)
            if (availableStock == null || availableStock <= 0) {
                log.error("Out of stock for productId: {}", itemDto.getProductId());
                throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "Product is out of stock: " + itemDto.getProductId());
            }

            if (availableStock < itemDto.getQuantity()) {
                log.error("Insufficient stock. Product: {}, Requested: {}, Available: {}",
                        itemDto.getProductId(), itemDto.getQuantity(), availableStock);
                throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST,
                        "Insufficient stock for: " + itemDto.getProductId() + ". Available: " + availableStock);
            }

            // Calculation of Subtotal and Grand Total
            BigDecimal subTotal = unitPrice.multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            grandTotal = grandTotal.add(subTotal);

            // Prepare the Model for the Repo
            OrderItemModel itemModel = new OrderItemModel();
            itemModel.setProductId(itemDto.getProductId());
            itemModel.setQuantity(itemDto.getQuantity());
            itemModel.setUnitPrice(unitPrice);
            itemModel.setSubTotal(subTotal);
            itemModel.setStatus("ACTIVE");

            // Add to list
            itemsToSave.add(itemModel);
        }

        // save Grand Total to Order Table
        OrderModel savedOrder = orderDomainRepo.saveOrder(
                request.getUserId(),
                request.getDeliveryAddressId(),
                grandTotal);


        // Save All Items linked to the new Order UUID
        orderDomainRepo.saveAllItems(savedOrder.getUuid(), itemsToSave);
        OrderModel updatedOrder = orderDomainRepo.getOneOrder(savedOrder.getUuid()).get();

        log.info("Finished {} created order and  successfully with total {}", CommonUtils.toJsonString(updatedOrder), grandTotal);
        return ResponseModel.success(addressNameResponse(updatedOrder));
    }

    @Override
    @Transactional
    public ResponseModel<OrderResponse> confirmOrder(UUID orderId) {
        log.info("Start: Confirm Order with ID: {}", orderId);

        // Validation for already confirmed
        orderDomainRepo.getOneOrder(orderId).filter(o -> o.getStatus() != OrderStatusEnum.PROCESSING).orElseThrow(() -> {
            log.error("Order is already confirmed!");
            return new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "Order is already confirmed .");
        });

        // Validation Getting all by OrderId
        List<OrderItemModel> items = orderDomainRepo.findAllByOrderId(orderId);

        // Prepare for Item & cut stock
        for (OrderItemModel orderItem : items) {
            log.info("Cutting  stock for Product ID: {}, Quantity: {} from inventory service", orderItem.getProductId(), orderItem.getQuantity());
            try {

                // Call Inventory Service to cut stock
                InventoryRequest cutStockReq = new InventoryRequest(orderItem.getProductId(), orderItem.getQuantity());

                // Deduct stock
                inventoryFeignClient.cutStock(cutStockReq);
                log.info("Finished deduction Stock with successful for product: {}", orderItem.getProductId());

            } catch (Exception e) {
                log.error("Critical error: Failed to deduct stock for product {}. Error: {}", orderItem.getProductId(), e.getMessage());
                throw new ApiException(ResponseConstants.ResponseStatus.INTERNAL_SERVER_ERROR, "Stock deduction failed for product: " + orderItem.getProductId());
            }
        }

        // Update status to PROCESSING
        orderDomainRepo.updateStatus(orderId, OrderStatusEnum.PROCESSING);
        OrderModel modelResponse = orderDomainRepo.getOneOrder(orderId).get();

        log.info("Finished: Confirm & Updated order status to PROCESSING with response: {} for ID: {}", CommonUtils.toJsonString(modelResponse), orderId);
        return ResponseModel.success(addressNameResponse(modelResponse));
    }

    @Override
    @Transactional
    public ResponseModel<OrderResponse> shipOrder(UUID orderId) {
        log.info("Start: Shipping Order with ID: {}", orderId);

        // 1. Fetch the order first
        OrderModel order = orderDomainRepo.getOneOrder(orderId).orElseThrow(() -> new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Order not found"));

        // 2. Check if it is already Shipped
        if (order.getStatus() == OrderStatusEnum.SHIPPED) {
            log.error("Order {} is already shipped.", orderId);
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, String.format("Order ID: %s is already shipped", orderId));
        }

        // 3. Validate for order in PROCESSING
        if (order.getStatus() != OrderStatusEnum.PROCESSING) {
            log.error("Order {} status is {}, cannot ship.", orderId, order.getStatus());
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "Order must be in PROCESSING status before it can be shipped.");
        }

        // 4. Update status to shipped
        orderDomainRepo.updateStatus(orderId, OrderStatusEnum.SHIPPED);
        OrderModel updatedOrder = orderDomainRepo.getOneOrder(orderId).get();

        log.info("Finished: Order status changed to Shipped : {} for ID: {}", CommonUtils.toJsonString(updatedOrder), orderId);
        return ResponseModel.success(addressNameResponse(updatedOrder));
    }

    @Override
    @Transactional
    public ResponseModel<OrderResponse> deliverOrder(UUID orderId) {
        log.info("Start: Update Order status to Delivered with ID: {}", orderId);

        // 1. Fetch the order first to ensure it exists
        OrderModel order = orderDomainRepo.getOneOrder(orderId).orElseThrow(() -> new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Order not found"));

        // 2. If it's already DELIVERED
        if (order.getStatus() == OrderStatusEnum.DELIVERED) {
            log.info("Order {} is already marked as DELIVERED", orderId);
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, String.format("Order ID: %s is already marked as DELIVERED", orderId));
        }

        // 3. Validate that the previous state was SHIPPED
        if (order.getStatus() != OrderStatusEnum.SHIPPED) {
            log.error("Cannot deliver order {}. Current status: {}", orderId, order.getStatus());
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "Order must be SHIPPED before it can be marked as DELIVERED.");
        }

        // 4. Perform the update
        orderDomainRepo.updateStatus(orderId, OrderStatusEnum.DELIVERED);
        OrderModel updatedOrder = orderDomainRepo.getOneOrder(orderId).get();

        log.info("Finish: Changed Order :{} status to DELIVERED for ID: {}",CommonUtils.toJsonString(updatedOrder), orderId);
        return ResponseModel.success(addressNameResponse(updatedOrder));
    }

    @Override
    public ResponseModel<List<TrackModel>> trackOrder(UUID orderId) {
        log.info("Start: Getting Track status history for order ID: {}", orderId);

        // 1. Validate order id
        OrderModel orderModel = orderDomainRepo.getOneOrder(orderId).orElseThrow(() -> {
            log.error("Order id: {} is not found!", orderId);
            throw new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Order id: {} not found!", orderId);
        });

        // 3. call domainRepo for tracking
        List<TrackModel> history = orderDomainRepo.trackingStatus(orderModel.getUuid());

        log.info("Finished: Getting Track status for ID: {}", orderModel.getUuid());
        return ResponseModel.success(history);
    }

    @Override
    @Transactional
    public ResponseModel<Void> cancelOrder(UUID orderId) {
        log.info("Start: Request to cancel (and delete) Order ID: {}", orderId);

        // 1. Fetch the order to check its current state
        OrderModel order = orderDomainRepo.getOneOrder(orderId).orElseThrow(() -> new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Order not found"));

        // 2. Only PENDING orders can be Cancel
        if (order.getStatus() != OrderStatusEnum.PENDING) {
            log.error("Cannot cancel order {}. Only PENDING orders can be cancel", orderId);
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "Order cannot be cancelled because it is already " + order.getStatus());
        }

        // 3. Call the domain for delete
        orderDomainRepo.cancelOrder(orderId);
        log.info("Finished: Order {} has been permanently Cancel", orderId);
        return ResponseModel.success();
    }

    @Override
    public ResponseModel<List<SalesByProvinceResponse>> getSalesByProvince() {
        log.info("Start: Getting sales summary by province");

        try {
            // 1. Fetch raw sales data from repository
            List<Map<String, Object>> rawSalesData = orderDomainRepo.getSalesByProvinceRaw();

            if (rawSalesData == null || rawSalesData.isEmpty()) {
                log.warn("No sales data found by province");
                return ResponseModel.success(Collections.emptyList());
            }

            // 2. Extract address IDs from the result
            List<UUID> addressIds = new ArrayList<>();
            for (Map<String, Object> row : rawSalesData) {
                Object addressIdObj = row.get("addressId");
                if (addressIdObj != null) {
                    try {
                        addressIds.add(UUID.fromString(addressIdObj.toString()));
                    } catch (IllegalArgumentException e) {
                        log.warn("Invalid address ID format: {}", addressIdObj);
                    }
                }
            }

            // 3. Fetch address details to get province names
            Map<UUID, AddressResponse> addressMap = fetchAddressMap(addressIds);

            // 4. Transform raw data to response with province enrichment
            Map<String, SalesByProvinceResponse> provinceMap = new LinkedHashMap<>();

            for (Map<String, Object> row : rawSalesData) {
                String addressIdStr = row.get("addressId") != null ? row.get("addressId").toString() : null;

                if (addressIdStr != null && !addressIdStr.isEmpty()) {
                    try {
                        UUID addressId = UUID.fromString(addressIdStr);
                        AddressResponse address = addressMap.get(addressId);

                        String provinceName = (address != null && address.getProvinceNameKh() != null)
                            ? address.getProvinceNameKh()
                            : "Unknown";

                        Long totalOrders = convertToLong(row.get("totalOrders"));
                        BigDecimal totalSales = convertToBigDecimal(row.get("totalSales"));

                        // If province already exists, accumulate
                        if (provinceMap.containsKey(provinceName)) {
                            SalesByProvinceResponse existing = provinceMap.get(provinceName);
                            existing.setTotalOrders(existing.getTotalOrders() + totalOrders);
                            existing.setTotalSales(existing.getTotalSales().add(totalSales));
                        } else {
                            provinceMap.put(provinceName, SalesByProvinceResponse.builder()
                                    .province(provinceName)
                                    .totalOrders(totalOrders)
                                    .totalSales(totalSales)
                                    .build());
                        }
                    } catch (IllegalArgumentException e) {
                        log.warn("Failed to parse address ID: {}", addressIdStr);
                    }
                }
            }

            List<SalesByProvinceResponse> result = new ArrayList<>(provinceMap.values());

            log.info("Finish: Retrieved sales data for {} provinces", result.size());
            return ResponseModel.success(result);

        } catch (Exception e) {
            log.error("Error getting sales by province: {}", e.getMessage(), e);
            throw new ApiException(ResponseConstants.ResponseStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve sales by province");
        }
    }

    /**
     * Helper method to safely convert Object to Long
     */
    private Long convertToLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof Number) return ((Number) value).longValue();
        try {
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            log.warn("Failed to convert {} to Long", value);
            return 0L;
        }
    }

    /**
     * Helper method to safely convert Object to BigDecimal
     */
    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return new BigDecimal(((Number) value).doubleValue());
        try {
            return new BigDecimal(value.toString());
        } catch (Exception e) {
            log.warn("Failed to convert {} to BigDecimal", value);
            return BigDecimal.ZERO;
        }
    }


    /**
     * This helper method performs data enrichment by converting a list of Address IDs
     */
    private Map<UUID, AddressResponse> fetchAddressMap(List<UUID> addressIds) {
        if (addressIds == null || addressIds.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            // Convert UUIDs to Strings for the Feign Client call
            List<String> idsAsStrings = addressIds.stream()
                    .map(UUID::toString)
                    .distinct()
                    .toList();

            // Call External Address Service
            ResponseModel<List<AddressResponse>> response = addressFeignClient.getAddressByIds(idsAsStrings);

            // Convert the response list into a Map for O(1) access time
            if (response != null && response.getData() != null) {
                return response.getData().stream()
                        .collect(Collectors.toMap(
                                AddressResponse::getUuid,
                                addr -> addr,
                                (existing, replacement) -> existing // Handle duplicate keys safely
                        ));
            }
        } catch (Exception e) {
            // Log error but do not crash the main thread (Graceful Degradation)
            log.error("Address Service communication failed: {}", e.getMessage());
        }

        return Collections.emptyMap();
    }

    /**
     * NEW HELPER METHOD: To avoid code duplication
     * This maps the model and adds the address summary from the Feign Client
     */
    private OrderResponse addressNameResponse(OrderModel model) {

        // map model to response
        OrderResponse response = orderMapper.toOrderResponse(model);

        // validate if delivery address id is present before calling the feign client
        if (model.getDeliveryAddressId() != null) {
            Map<UUID, AddressResponse> addressMap = fetchAddressMap(List.of(model.getDeliveryAddressId()));
            AddressResponse addr = addressMap.get(model.getDeliveryAddressId());

            // Set the summary field in the response
            if (addr != null) {
                response.setDeliveryAddressSummary(addr.getProvinceNameKh() + ", " + addr.getCountry());
            } else {
                response.setDeliveryAddressSummary("N/A");
            }
        }
        return response;
    }

}
