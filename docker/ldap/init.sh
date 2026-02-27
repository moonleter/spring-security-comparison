#!/bin/bash
set -e

mkdir -p /container/service/slapd/assets/config/bootstrap/ldif/custom

sed -e "s|\${LDAP_ADMIN_PASSWORD}|${LDAP_ADMIN_PASSWORD}|g" \
    -e "s|\${LDAP_USER_PASSWORD1}|${LDAP_USER_PASSWORD1}|g" \
    -e "s|\${LDAP_USER_PASSWORD2}|${LDAP_USER_PASSWORD2}|g" \
    /ldif-templates/bootstrap.ldif > /container/service/slapd/assets/config/bootstrap/ldif/custom/bootstrap.ldif

exec /container/tool/run