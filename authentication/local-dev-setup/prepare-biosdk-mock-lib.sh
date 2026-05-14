#!/bin/bash
# prepare-biosdk-mock-lib.sh
#
# Downloads the BioSDK mock JAR from Maven Central and packages it as mock-sdk.zip
# inside the WireMock __files directory so that biosdk-service can fetch it on startup.
#
# Run this script ONCE before the first "docker compose up".
# Re-run only if you need to refresh the file.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TARGET_DIR="$SCRIPT_DIR/docker-compose/wiremock/__files"
JAR_URL="https://repo1.maven.org/maven2/io/mosip/mock/sdk/mock-sdk/1.3.0-beta.1/mock-sdk-1.3.0-beta.1-jar-with-dependencies.jar"
JAR_NAME="mock-sdk-1.3.0-beta.1-jar-with-dependencies.jar"
ZIP_NAME="mock-sdk.zip"
TEMP_DIR=$(mktemp -d)

cleanup() {
    rm -rf "$TEMP_DIR"
}
trap cleanup EXIT

echo "========================================"
echo " BioSDK Mock Library Preparation"
echo "========================================"
echo ""

# Check for curl
if ! command -v curl &>/dev/null; then
    echo "ERROR: 'curl' is not installed. Install it and re-run."
    exit 1
fi

# Check for zip
if ! command -v zip &>/dev/null; then
    echo "ERROR: 'zip' is not installed. Install it and re-run."
    echo "  macOS:  brew install zip"
    echo "  Ubuntu: sudo apt-get install zip"
    exit 1
fi

echo "[1/3] Downloading mock-sdk JAR from Maven Central (~110 MB)..."
curl -L --progress-bar "$JAR_URL" -o "$TEMP_DIR/$JAR_NAME"
echo "      Download complete."
echo ""

echo "[2/3] Packaging as $ZIP_NAME..."
(cd "$TEMP_DIR" && zip "$ZIP_NAME" "$JAR_NAME")
echo "      Zip created."
echo ""

echo "[3/3] Copying to WireMock __files directory..."
mkdir -p "$TARGET_DIR"
cp "$TEMP_DIR/$ZIP_NAME" "$TARGET_DIR/$ZIP_NAME"
echo "      Copied to: $TARGET_DIR/$ZIP_NAME"
echo ""

echo "========================================"
echo " Done! biosdk-service is ready to start."
echo "========================================"
echo ""
echo "Next step:"
echo "  cd local-dev-setup/docker-compose"
echo "  docker compose up -d"
