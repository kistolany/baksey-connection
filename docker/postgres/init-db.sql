-- Create postgres user and databases for the services
-- Note: postgres user already exists by default, just ensure password is set
ALTER USER postgres WITH PASSWORD 'postgres';

-- Drop existing databases if they exist
DROP DATABASE IF EXISTS baksey_service;
DROP DATABASE IF EXISTS inventory_service;
DROP DATABASE IF EXISTS order_service;
DROP DATABASE IF EXISTS shopping_cart_service;
DROP DATABASE IF EXISTS address_service;

-- Create databases
CREATE DATABASE baksey_service OWNER postgres;
CREATE DATABASE inventory_service OWNER postgres;
CREATE DATABASE order_service OWNER postgres;
CREATE DATABASE shopping_cart_service OWNER postgres;
CREATE DATABASE address_service OWNER postgres;

-- Drop and recreate users (use lowercase names for consistency)
DROP USER IF EXISTS inventory_user;
CREATE USER inventory_user WITH PASSWORD 'inventory@3142';

DROP USER IF EXISTS postgresdb;
CREATE USER postgresdb WITH PASSWORD 'postgres';

-- Change database owners to their respective users
ALTER DATABASE inventory_service OWNER TO inventory_user;
ALTER DATABASE order_service OWNER TO postgresdb;
ALTER DATABASE shopping_cart_service OWNER TO postgresdb;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE baksey_service TO postgres;
GRANT ALL PRIVILEGES ON DATABASE inventory_service TO inventory_user;
GRANT ALL PRIVILEGES ON DATABASE order_service TO postgresdb;
GRANT ALL PRIVILEGES ON DATABASE shopping_cart_service TO postgresdb;
GRANT ALL PRIVILEGES ON DATABASE address_service TO postgres;

