#!/bin/sh
# The certificates to consider should be in 'certs'.

# Location of active OpenSSL.
echo `openssl version -d`
OPENSSLDIR="/private/etc/ssl"
cd ${OPENSSLDIR}/certs

for CERTFILE in *; do
  # make sure file exists and is a valid cert
  test -f "$CERTFILE" || continue
  echo $CERTFILE
  HASH=$(openssl x509 -noout -hash -in "$CERTFILE")
  test -n "$HASH" || continue

  # use lowest available iterator for symlink
  for ITER in 0 1 2 3 4 5 6 7 8 9; do
    test -f "${HASH}.${ITER}" && continue
    ln -s "$CERTFILE" "${HASH}.${ITER}"
    test -L "${HASH}.${ITER}" && break
  done
done
