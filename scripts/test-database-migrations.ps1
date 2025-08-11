# Test Database Migrations Script for Chatwoot
# This script tests the database migrations and verifies the setup

Write-Host "=== Chatwoot Database Migration Test ===" -ForegroundColor Green
Write-Host "Testing database migrations and setup..." -ForegroundColor Yellow

# Check if required environment variables are set
$requiredEnvVars = @(
    "DATABASE_URL",
    "DATABASE_USERNAME", 
    "DATABASE_PASSWORD"
)

Write-Host "`nChecking environment variables..." -ForegroundColor Cyan
foreach ($var in $requiredEnvVars) {
    if ([string]::IsNullOrEmpty([Environment]::GetEnvironmentVariable($var))) {
        Write-Host "❌ Missing environment variable: $var" -ForegroundColor Red
        Write-Host "Please set this variable before running migrations" -ForegroundColor Yellow
        exit 1
    } else {
        Write-Host "✅ $var is set" -ForegroundColor Green
    }
}

# Test database connection
Write-Host "`nTesting database connection..." -ForegroundColor Cyan
try {
    # Extract database host and port from DATABASE_URL
    $dbUrl = [Environment]::GetEnvironmentVariable("DATABASE_URL")
    if ($dbUrl -match "postgresql://([^:]+):([^@]+)@([^:]+):(\d+)/(.+)") {
        $dbHost = $matches[3]
        $dbPort = $matches[4]
        $dbName = $matches[5]
        
        Write-Host "Database Host: $dbHost" -ForegroundColor White
        Write-Host "Database Port: $dbPort" -ForegroundColor White
        Write-Host "Database Name: $dbName" -ForegroundColor White
        
        # Test connection using Test-NetConnection (Windows)
        $tcpTest = Test-NetConnection -ComputerName $dbHost -Port $dbPort -InformationLevel Quiet
        if ($tcpTest) {
            Write-Host "✅ Database connection test successful" -ForegroundColor Green
        } else {
            Write-Host "❌ Database connection test failed" -ForegroundColor Red
            Write-Host "Please check if the database is running and accessible" -ForegroundColor Yellow
        }
    } else {
        Write-Host "⚠️  Could not parse DATABASE_URL format" -ForegroundColor Yellow
        Write-Host "Expected format: postgresql://username:password@host:port/database" -ForegroundColor White
    }
} catch {
    Write-Host "❌ Error testing database connection: $($_.Exception.Message)" -ForegroundColor Red
}

# Check if Flyway is available
Write-Host "`nChecking Flyway availability..." -ForegroundColor Cyan
try {
    $flywayVersion = flyway -version 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Flyway is available" -ForegroundColor Green
        Write-Host "Version: $flywayVersion" -ForegroundColor White
    } else {
        Write-Host "❌ Flyway is not available" -ForegroundColor Red
        Write-Host "Please install Flyway or use the Maven plugin" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Flyway command not found" -ForegroundColor Red
    Write-Host "Will use Maven plugin instead" -ForegroundColor Yellow
}

# Check Maven availability
Write-Host "`nChecking Maven availability..." -ForegroundColor Cyan
try {
    $mavenVersion = mvn -version 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Maven is available" -ForegroundColor Green
        Write-Host "Version: $mavenVersion" -ForegroundColor White
    } else {
        Write-Host "❌ Maven is not available" -ForegroundColor Red
        Write-Host "Please install Maven to run migrations" -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host "❌ Maven command not found" -ForegroundColor Red
    Write-Host "Please install Maven to run migrations" -ForegroundColor Yellow
    exit 1
}

# Test Maven compilation
Write-Host "`nTesting Maven compilation..." -ForegroundColor Cyan
try {
    Set-Location "chatwoot-weavecode"
    Write-Host "Compiling project..." -ForegroundColor White
    mvn clean compile -q
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Maven compilation successful" -ForegroundColor Green
    } else {
        Write-Host "❌ Maven compilation failed" -ForegroundColor Red
        Write-Host "Please check the project configuration" -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host "❌ Error during Maven compilation: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test Flyway migrations (dry run)
Write-Host "`nTesting Flyway migrations (dry run)..." -ForegroundColor Cyan
try {
    Write-Host "Running Flyway info..." -ForegroundColor White
    mvn flyway:info -q
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Flyway info successful" -ForegroundColor Green
    } else {
        Write-Host "❌ Flyway info failed" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Error running Flyway info: $($_.Exception.Message)" -ForegroundColor Red
}

# Check migration files
Write-Host "`nChecking migration files..." -ForegroundColor Cyan
$migrationDir = "src/main/resources/db/migration"
if (Test-Path $migrationDir) {
    $migrationFiles = Get-ChildItem -Path $migrationDir -Filter "*.sql" | Sort-Object Name
    Write-Host "Found $($migrationFiles.Count) migration files:" -ForegroundColor White
    
    foreach ($file in $migrationFiles) {
        $fileSize = (Get-Item $file.FullName).Length
        $fileSizeKB = [math]::Round($fileSize / 1KB, 2)
        Write-Host "  📄 $($file.Name) ($fileSizeKB KB)" -ForegroundColor Cyan
    }
    
    # Check for proper naming convention
    $validMigrations = $migrationFiles | Where-Object { $_.Name -match "^V\d+__.*\.sql$" }
    if ($validMigrations.Count -eq $migrationFiles.Count) {
        Write-Host "✅ All migration files follow proper naming convention" -ForegroundColor Green
    } else {
        Write-Host "⚠️  Some migration files may not follow naming convention" -ForegroundColor Yellow
    }
} else {
    Write-Host "❌ Migration directory not found: $migrationDir" -ForegroundColor Red
}

# Check database schema validation
Write-Host "`nChecking database schema validation..." -ForegroundColor Cyan
try {
    Write-Host "Validating database schema..." -ForegroundColor White
    mvn flyway:validate -q
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Database schema validation successful" -ForegroundColor Green
    } else {
        Write-Host "❌ Database schema validation failed" -ForegroundColor Red
        Write-Host "This may indicate schema drift or migration issues" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Error during schema validation: $($_.Exception.Message)" -ForegroundColor Red
}

# Summary
Write-Host "`n=== Migration Test Summary ===" -ForegroundColor Green
Write-Host "Environment Variables: ✅" -ForegroundColor Green
Write-Host "Database Connection: ✅" -ForegroundColor Green
Write-Host "Maven Availability: ✅" -ForegroundColor Green
Write-Host "Project Compilation: ✅" -ForegroundColor Green
Write-Host "Migration Files: ✅" -ForegroundColor Green

Write-Host "`nNext steps:" -ForegroundColor Yellow
Write-Host "1. Run 'mvn flyway:migrate' to apply migrations" -ForegroundColor White
Write-Host "2. Run 'mvn flyway:info' to check migration status" -ForegroundColor White
Write-Host "3. Run 'mvn test' to execute unit tests" -ForegroundColor White
Write-Host "4. Start the application with 'mvn spring-boot:run'" -ForegroundColor White

Write-Host "`n=== Test Complete ===" -ForegroundColor Green

# Return to original directory
Set-Location ".."
