#!/bin/sh
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
docker run -t --rm --name generate-testdata-generator-ui -v ${SCRIPT_DIR}/../../testdata-generator-ui:/src node:lts-alpine sh -c 'cd /src;export API_URL=/api; npm install && npm run-script build'
rm -Rf ${SCRIPT_DIR}/../src/main/resources/static-web/ui
mv ${SCRIPT_DIR}/../../testdata-generator-ui/dist ${SCRIPT_DIR}/../src/main/resources/static-web/ui
