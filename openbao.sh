#!/bin/bash
# Sample script that will get and store secret

jq_not_installed() { echo "jq not installed" 1>&2; exit 1; }

not_found() { local COMMENT="$1"; echo "${COMMENT} not found" 1>&2; exit 1; }

# Load environment variables from .env file
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
else
    not_found ".env"
fi

HOST=${VAULT_URL}
NAMESPACE=${VAULT_PATH}/${SPRING_PROFILES_ACTIVE}
ROLE_ID=${VAULT_ROLE_ID}
SECRET_ID=${VAULT_SECRET_ID}
SECRET_JSON=${VAULT_SECRET_JSON}

check_status_code() {
    local COMMENT="$1"
    local STATUS_CODE="$2"

    if [[ "${STATUS_CODE}" == "200" ]]; then
        echo "✅ ${COMMENT} created successfully."
    elif [[ "${STATUS_CODE}" == "204" ]]; then
        echo "✅ ${COMMENT} created successfully."
    elif [[ "${STATUS_CODE}" == "400" ]]; then
        echo "📌 ${COMMENT} already exists."
    else
        echo "❌ Failed to create ${COMMENT}. HTTP status: ${STATUS_CODE}"
        exit 1
    fi
}

store_secret() {
    local NAMESPACE="$1"
    local TOKEN="$2"
    local JSON_FILE="$3"
    local STATUS_CODE

    STATUS_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
        -X PUT "${HOST}/v1/secret/data/${NAMESPACE}" \
        -H "X-Vault-Token: ${TOKEN}" \
        -d "@${JSON_FILE}")

    check_status_code "Secret" ${STATUS_CODE}
}

get_secret() {
    local NAMESPACE="$1"
    local TOKEN="$2"
    local RESPONSE
    local SECRET

    RESPONSE=$(curl -s -X GET \
        "${HOST}/v1/secret/data/${NAMESPACE}" \
        -H "X-Vault-Token: ${TOKEN}")

    # Extract secret using jq
    SECRET=$(echo "${RESPONSE}" | jq -r .data.data)

    if [[ "${SECRET}" ]]; then
        echo "🗝️ Namespace '${NAMESPACE}' Secret: ${SECRET}"
    fi
}

get_token() {
    local NAMESPACE="$1"
    local ROLE_ID="$2"
    local SECRET_ID="$3"
    local RESPONSE

    RESPONSE=$(curl -s -X POST \
        "${HOST}/v1/auth/approle/login" \
        -d "{\"role_id\": \"${ROLE_ID}\", \"secret_id\": \"${SECRET_ID}\"}")

    # Extract role_id using jq
    TOKEN=$(echo "${RESPONSE}" | jq -r .auth.client_token)

    if [[ "${TOKEN}" ]]; then
        echo "🧩 Role ID '${ROLE_ID}' Token: ${TOKEN}"
    fi
}

main() {
    get_token ${NAMESPACE} ${ROLE_ID} ${SECRET_ID}
    get_secret ${NAMESPACE} ${TOKEN}
    # store_secret ${NAMESPACE} ${TOKEN} ${SECRET_JSON}
    # get_secret ${NAMESPACE} ${TOKEN}
}

# Run the script
main
