#!/bin/bash

echo "========================================="
echo "TESTING LANHCARE API"
echo "========================================="

# Test 1: Health Check
echo ""
echo "1. Testing Health Check..."
curl -s http://localhost:8080/api/auth/health
echo ""

# Test 2: Register
echo ""
echo "2. Testing Register..."
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@example.com","fullname":"Test User","password":"password123"}'
echo ""

# Test 3: Login
echo ""
echo "3. Testing Login..."
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@example.com","password":"password123"}'
echo ""

echo ""
echo "========================================="
echo "DONE!"
echo "========================================="
