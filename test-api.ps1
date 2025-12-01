Write-Host "=========================================" -ForegroundColor Green
Write-Host "TESTING LANHCARE API" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green

# Test 1: Health Check
Write-Host "`n1. Testing Health Check..." -ForegroundColor Yellow
curl.exe -s http://localhost:8080/api/auth/health
Write-Host ""

# Test 2: Register
Write-Host "`n2. Testing Register..." -ForegroundColor Yellow
$registerBody = @{
    email = "testuser@example.com"
    fullname = "Test User"
    password = "password123"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" `
    -Method Post `
    -ContentType "application/json" `
    -Body $registerBody `
    -ErrorAction SilentlyContinue

if ($response) {
    Write-Host "Register successful!" -ForegroundColor Green
    $response | ConvertTo-Json -Depth 10
    $token = $response.accessToken
} else {
    Write-Host "Register failed or user already exists" -ForegroundColor Red
}

# Test 3: Login
Write-Host "`n3. Testing Login..." -ForegroundColor Yellow
$loginBody = @{
    email = "testuser@example.com"
    password = "password123"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
    -Method Post `
    -ContentType "application/json" `
    -Body $loginBody

Write-Host "Login successful!" -ForegroundColor Green
$response | ConvertTo-Json -Depth 10
$token = $response.accessToken

# Test 4: Get Current User (using token)
if ($token) {
    Write-Host "`n4. Testing Get Current User..." -ForegroundColor Yellow
    try {
        $headers = @{
            "Authorization" = "Bearer $token"
        }
        $userResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/accounts/me" `
            -Method Get `
            -Headers $headers
        
        Write-Host "Get current user successful!" -ForegroundColor Green
        $userResponse | ConvertTo-Json -Depth 10
    } catch {
        Write-Host "Get current user failed: $_" -ForegroundColor Red
    }
}

Write-Host "`n=========================================" -ForegroundColor Green
Write-Host "DONE!" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green
