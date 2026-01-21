#!/bin/bash
echo "Building SignalRoot Backend for Vercel..."
mvn clean package -DskipTests
echo "Build completed!"
