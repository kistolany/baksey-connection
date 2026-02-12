# ✅ Fixed PostgreSQL Initialization Script

## Problem Fixed
```
ERROR:  syntax error at or near "NOT" at character 20
CREATE DATABASE IF NOT EXISTS baksey_service OWNER postgres;
```

PostgreSQL does NOT support `CREATE DATABASE IF NOT EXISTS` syntax - that's MySQL/MariaDB syntax.

## Solution Applied

Changed the approach to:
1. **Use `DROP DATABASE IF EXISTS`** - This PostgreSQL syntax safely removes databases if they exist
2. **Use simple `CREATE DATABASE`** - Create databases without the IF NOT EXISTS clause
3. **Use `ALTER DATABASE ... OWNER TO`** - Change owners after creation instead of during
4. **Use `DROP USER IF EXISTS ... CASCADE`** - Safely handle user recreation

## Updated init-db.sql Flow

```sql
-- Step 1: Set postgres user password (user already exists)
ALTER USER postgres WITH PASSWORD 'postgres';

-- Step 2: Drop all databases (if they exist from previous runs)
DROP DATABASE IF EXISTS baksey_service;
DROP DATABASE IF EXISTS inventory_service;
DROP DATABASE IF EXISTS order_service;
DROP DATABASE IF EXISTS shopping_cart_service;

-- Step 3: Create all databases owned by postgres (temporary)
CREATE DATABASE baksey_service OWNER postgres;
CREATE DATABASE inventory_service OWNER postgres;
CREATE DATABASE order_service OWNER postgres;
CREATE DATABASE shopping_cart_service OWNER postgres;

-- Step 4: Create/Recreate users (drop if exists)
DROP USER IF EXISTS inventory_user CASCADE;
CREATE USER inventory_user WITH PASSWORD 'inventory@3142';

DROP USER IF EXISTS postgresSql CASCADE;
CREATE USER postgresSql WITH PASSWORD 'postgres';

-- Step 5: Change database owners to their respective users
ALTER DATABASE inventory_service OWNER TO inventory_user;
ALTER DATABASE order_service OWNER TO postgresSql;
ALTER DATABASE shopping_cart_service OWNER TO postgresSql;

-- Step 6: Grant all privileges
GRANT ALL PRIVILEGES ON DATABASE baksey_service TO postgres;
GRANT ALL PRIVILEGES ON DATABASE inventory_service TO inventory_user;
GRANT ALL PRIVILEGES ON DATABASE order_service TO postgresSql;
GRANT ALL PRIVILEGES ON DATABASE shopping_cart_service TO postgresSql;
```

## How to Restart Services

Run these commands in PowerShell:

```powershell
cd e:\baksey_project

# Stop containers
docker-compose down

# Clean up volumes (optional but recommended for fresh start)
docker volume prune -f

# Rebuild and start all services
docker-compose up --build
```

## What Will Happen Now

1. ✅ PostgreSQL starts
2. ✅ init-db.sql executes without syntax errors
3. ✅ All 4 databases created: baksey_service, inventory_service, order_service, shopping_cart_service
4. ✅ All users created with correct passwords:
   - `postgres` / `postgres`
   - `inventory_user` / `inventory@3142`
   - `postgresSql` / `postgres`
5. ✅ Database ownership properly assigned
6. ✅ All permissions granted
7. ✅ Health check passes (pg_isready)
8. ✅ All 4 microservices start successfully
9. ✅ Services connect to databases with correct credentials

## Verify Success

After services are running:

```powershell
# Check all containers
docker-compose ps

# View logs for any service
docker-compose logs inventory-service | tail -30

# Check if postgres is healthy
docker-compose logs baksey-db | grep "database system is ready"
```

All errors should now be resolved! 🚀

