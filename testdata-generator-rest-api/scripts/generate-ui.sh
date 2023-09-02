#!/bin/sh
#
# Copyright 2022-2023 benelog GmbH & Co. KG
#
#     Licensed under the Apache License, Version 2.0 (the "License");
#     you may not use this file except in compliance with the License.
#     You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
#     Unless required by applicable law or agreed to in writing, software
#     distributed under the License is distributed on an "AS IS" BASIS,
#     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#     See the License for the specific language governing permissions and
#     limitations under the License.
#

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
docker run -t --rm --name generate-testdata-generator-ui -v ${SCRIPT_DIR}/../../testdata-generator-ui:/src node:lts-alpine sh -c 'cd /src;export API_URL=/api; npm install && npm run-script build'
rm -Rf ${SCRIPT_DIR}/../src/main/resources/static-web/ui
mv ${SCRIPT_DIR}/../../testdata-generator-ui/dist ${SCRIPT_DIR}/../src/main/resources/static-web/ui
