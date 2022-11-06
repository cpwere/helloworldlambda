#!/bin/bash
echo "Verifying resources ..."
LABEL_ACTION_TYPE="Docker container services verification"
echo
echo " ***********************  " $LABEL_ACTION_TYPE "  ************************** "
echo ""

docker container ls

echo " *********************** DONE: verifying container services  *********************** "


