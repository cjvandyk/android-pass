#!/bin/bash

set -eu

echo "🪛 Overriding CI Gradle properties"
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
REPO_ROOT=$(echo "${SCRIPT_DIR}" | sed 's:tools/ci::g')
PROPERTIES_FILE="$REPO_ROOT/gradle.properties"
CI_PROPERTIES_FILE="$SCRIPT_DIR/ci-gradle.properties"
rm "$PROPERTIES_FILE" && cp "$CI_PROPERTIES_FILE" "$PROPERTIES_FILE"
