#!/bin/bash

echo "=== Creating testing data for ride-sharing app ==="

# Base URL for the API
BASE_URL="http://localhost:8080/api"

# Create riders
echo "Creating riders..."
RIDER1=$(curl -s -X POST "$BASE_URL/riders" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Emma Wilson",
    "email": "emma@example.com",
    "phone": "5557778888",
    "paymentMethod": "CREDIT_CARD"
  }')
RIDER1_ID=$(echo $RIDER1 | grep -o '"id":"[^"]*' | cut -d'"' -f4)
echo "Created Rider 1 (Emma) with ID: $RIDER1_ID"

RIDER2=$(curl -s -X POST "$BASE_URL/riders" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Michael Brown",
    "email": "michael@example.com",
    "phone": "5559990000",
    "paymentMethod": "CASH"
  }')
RIDER2_ID=$(echo $RIDER2 | grep -o '"id":"[^"]*' | cut -d'"' -f4)
echo "Created Rider 2 (Michael) with ID: $RIDER2_ID"

RIDER3=$(curl -s -X POST "$BASE_URL/riders" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sophia Chen",
    "email": "sophia@example.com",
    "phone": "5551112222",
    "paymentMethod": "DEBIT_CARD"
  }')
RIDER3_ID=$(echo $RIDER3 | grep -o '"id":"[^"]*' | cut -d'"' -f4)
echo "Created Rider 3 (Sophia) with ID: $RIDER3_ID"

# Create drivers
echo -e "\nCreating drivers..."
DRIVER1=$(curl -s -X POST "$BASE_URL/drivers" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "David Lee",
    "email": "david@example.com",
    "phone": "5552223333",
    "licenseNumber": "DL111222",
    "vehicleModel": "Tesla Model 3",
    "vehiclePlate": "EV1000"
  }')
DRIVER1_ID=$(echo $DRIVER1 | grep -o '"id":"[^"]*' | cut -d'"' -f4)
echo "Created Driver 1 (David) with ID: $DRIVER1_ID"

DRIVER2=$(curl -s -X POST "$BASE_URL/drivers" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sofia Martinez",
    "email": "sofia@example.com",
    "phone": "5554445555",
    "licenseNumber": "DL333444",
    "vehicleModel": "Hyundai Ioniq",
    "vehiclePlate": "GRN123"
  }')
DRIVER2_ID=$(echo $DRIVER2 | grep -o '"id":"[^"]*' | cut -d'"' -f4)
echo "Created Driver 2 (Sofia) with ID: $DRIVER2_ID"

# Set driver locations
echo -e "\nUpdating driver locations..."
curl -s -X PUT "$BASE_URL/drivers/$DRIVER1_ID/location" \
  -H "Content-Type: application/json" \
  -d '{
    "latitude": 42.6977,
    "longitude": 23.3219,
    "address": "Sofia City Center"
  }' > /dev/null
echo "Updated Driver 1 location to Sofia City Center"

curl -s -X PUT "$BASE_URL/drivers/$DRIVER2_ID/location" \
  -H "Content-Type: application/json" \
  -d '{
    "latitude": 42.6950,
    "longitude": 23.3250,
    "address": "Sofia University"
  }' > /dev/null
echo "Updated Driver 2 location to Sofia University"

# Ensure drivers are available
echo -e "\nSetting drivers as available..."
curl -s -X PUT "$BASE_URL/drivers/$DRIVER1_ID/availability" \
  -H "Content-Type: application/json" \
  -d '{"available": true}' > /dev/null
echo "Driver 1 set to available"

curl -s -X PUT "$BASE_URL/drivers/$DRIVER2_ID/availability" \
  -H "Content-Type: application/json" \
  -d '{"available": true}' > /dev/null
echo "Driver 2 set to available"

# Create rides with different dates to test time-based queries
echo -e "\nCreating rides..."

# Ride 1: Yesterday - completed with payment
echo "Creating Ride 1 (yesterday - completed)..."
RIDE1=$(curl -s -X POST "$BASE_URL/rides" \
  -H "Content-Type: application/json" \
  -d "{
    \"riderId\": \"$RIDER1_ID\",
    \"pickupLatitude\": 42.6977,
    \"pickupLongitude\": 23.3219,
    \"pickupAddress\": \"Sofia City Center\",
    \"destLatitude\": 42.7057,
    \"destLongitude\": 23.3245,
    \"destAddress\": \"Sofia Business Park\"
  }")
RIDE1_ID=$(echo $RIDE1 | grep -o '"id":"[^"]*' | cut -d'"' -f4)
echo "Created Ride 1 with ID: $RIDE1_ID"

curl -s -X POST "$BASE_URL/rides/$RIDE1_ID/assign-driver" \
  -H "Content-Type: application/json" \
  -d "{\"driverId\": \"$DRIVER1_ID\"}" > /dev/null
echo "Assigned Driver 1 to Ride 1"

curl -s -X PUT "$BASE_URL/rides/$RIDE1_ID/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "PICKED_UP"}' > /dev/null
echo "Updated Ride 1 status to PICKED_UP"

curl -s -X PUT "$BASE_URL/rides/$RIDE1_ID/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "COMPLETED"}' > /dev/null
echo "Updated Ride 1 status to COMPLETED (generates transaction)"

# Ride 2: Today - completed with payment
echo -e "\nCreating Ride 2 (today - completed)..."
RIDE2=$(curl -s -X POST "$BASE_URL/rides" \
  -H "Content-Type: application/json" \
  -d "{
    \"riderId\": \"$RIDER2_ID\",
    \"pickupLatitude\": 42.6950,
    \"pickupLongitude\": 23.3250,
    \"pickupAddress\": \"Sofia University\",
    \"destLatitude\": 42.6505,
    \"destLongitude\": 23.3877,
    \"destAddress\": \"Sofia Airport\"
  }")
RIDE2_ID=$(echo $RIDE2 | grep -o '"id":"[^"]*' | cut -d'"' -f4)
echo "Created Ride 2 with ID: $RIDE2_ID"

curl -s -X POST "$BASE_URL/rides/$RIDE2_ID/assign-driver" \
  -H "Content-Type: application/json" \
  -d "{\"driverId\": \"$DRIVER2_ID\"}" > /dev/null
echo "Assigned Driver 2 to Ride 2"

curl -s -X PUT "$BASE_URL/rides/$RIDE2_ID/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "PICKED_UP"}' > /dev/null
echo "Updated Ride 2 status to PICKED_UP"

curl -s -X PUT "$BASE_URL/rides/$RIDE2_ID/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "COMPLETED"}' > /dev/null
echo "Updated Ride 2 status to COMPLETED (generates transaction)"

# Update driver availability after rides
curl -s -X PUT "$BASE_URL/drivers/$DRIVER1_ID/availability" \
  -H "Content-Type: application/json" \
  -d '{"available": true}' > /dev/null
echo "Driver 1 set back to available"

curl -s -X PUT "$BASE_URL/drivers/$DRIVER2_ID/availability" \
  -H "Content-Type: application/json" \
  -d '{"available": true}' > /dev/null
echo "Driver 2 set back to available"

# Ride 3: Today - cancelled
echo -e "\nCreating Ride 3 (today - cancelled)..."
RIDE3=$(curl -s -X POST "$BASE_URL/rides" \
  -H "Content-Type: application/json" \
  -d "{
    \"riderId\": \"$RIDER3_ID\",
    \"pickupLatitude\": 42.6960,
    \"pickupLongitude\": 23.3230,
    \"pickupAddress\": \"Sofia National Palace of Culture\",
    \"destLatitude\": 42.7000,
    \"destLongitude\": 23.3300,
    \"destAddress\": \"Sofia Tech Park\"
  }")
RIDE3_ID=$(echo $RIDE3 | grep -o '"id":"[^"]*' | cut -d'"' -f4)
echo "Created Ride 3 with ID: $RIDE3_ID"

curl -s -X PUT "$BASE_URL/rides/$RIDE3_ID/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "CANCELLED"}' > /dev/null
echo "Updated Ride 3 status to CANCELLED (no transaction generated)"

# Insert transactions directly to MongoDB for special cases
echo -e "\nInserting special transaction cases directly to MongoDB..."
cat <<EOT > /tmp/mongo_transactions.js
use ride_sharing_app_backend;

// Insert a failed transaction for Ride 2
db.transactions.insertOne({
  ride: { \$ref: "rides", \$id: ObjectId("$RIDE2_ID") },
  paymentMethod: "CASH",
  amount: 12.75,
  status: "FAILED",
  timestamp: new Date(),
  transactionReference: "TX-FAILED-001"
});

// Insert a refunded transaction
db.transactions.insertOne({
  ride: { \$ref: "rides", \$id: ObjectId("$RIDE1_ID") },
  paymentMethod: "CREDIT_CARD",
  amount: 15.50,
  status: "REFUNDED",
  timestamp: new Date(),
  transactionReference: "TX-REFUNDED-001"
});

// Transaction from last week with higher amount
let lastWeek = new Date();
lastWeek.setDate(lastWeek.getDate() - 7);

db.transactions.insertOne({
  ride: { \$ref: "rides", \$id: ObjectId("$RIDE1_ID") },
  paymentMethod: "CREDIT_CARD",
  amount: 35.25,
  status: "COMPLETED",
  timestamp: lastWeek,
  transactionReference: "TX-LASTWEEK-001"
});

// Transaction from last month 
let lastMonth = new Date();
lastMonth.setMonth(lastMonth.getMonth() - 1);

db.transactions.insertOne({
  ride: { \$ref: "rides", \$id: ObjectId("$RIDE1_ID") },
  paymentMethod: "CREDIT_CARD",
  amount: 42.80,
  status: "COMPLETED",
  timestamp: lastMonth,
  transactionReference: "TX-LASTMONTH-001"
});

print("Inserted special transaction test data");
EOT

echo "Executing MongoDB script to insert transactions..."
mongosh --quiet /tmp/mongo_transactions.js

echo -e "\n=== Test Data Creation Complete ==="
echo -e "\n=== Now Testing Transaction Endpoints ==="

# Test the transaction endpoints
echo -e "\n1. Testing GET transactions for Ride 1 (should include completed and refunded):"
curl -s "$BASE_URL/transactions/ride/$RIDE1_ID" | jq .

echo -e "\n2. Testing GET transactions for Ride 2 (should include completed and failed):"
curl -s "$BASE_URL/transactions/ride/$RIDE2_ID" | jq .

# Get today's date in ISO format for testing date ranges
TODAY=$(date +"%Y-%m-%dT00:00:00")
TOMORROW=$(date -v+1d +"%Y-%m-%dT00:00:00" 2>/dev/null || date -d "+1 day" +"%Y-%m-%dT00:00:00")
LAST_WEEK=$(date -v-7d +"%Y-%m-%dT00:00:00" 2>/dev/null || date -d "-7 days" +"%Y-%m-%dT00:00:00")
NEXT_YEAR=$(date -v+1y +"%Y-%m-%dT00:00:00" 2>/dev/null || date -d "+1 year" +"%Y-%m-%dT00:00:00")

echo -e "\n3. Testing revenue stats for today only:"
curl -s "$BASE_URL/transactions/stats/revenue?start=$TODAY&end=$TOMORROW" | jq .

echo -e "\n4. Testing revenue stats for last week until next year (all data):"
curl -s "$BASE_URL/transactions/stats/revenue?start=$LAST_WEEK&end=$NEXT_YEAR" | jq .

echo -e "\n5. Testing transaction status counts for today:"
curl -s "$BASE_URL/transactions/stats/status?start=$TODAY&end=$TOMORROW" | jq .

echo -e "\n6. Testing transaction status counts for all time:"
curl -s "$BASE_URL/transactions/stats/status?start=2020-01-01T00:00:00&end=2030-12-31T23:59:59" | jq .

echo -e "\n=== Transaction Testing Complete ==="