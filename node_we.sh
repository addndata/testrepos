#!/bin/bash
# Purpose of this script is to perform installation of CryptoPro distros
# It utilizes following ENV variables:
#   * CSP_VERSION – specifies CryptoPro CSP version (actually, the value is ignored)
#   * JCSP_VERSION – specifies CryptoPro JCSP version (actually, the value is ignored)
#   * EXPERIMENTAL_CRYPTO – triggers installation of experimental CSP and JCSP distros
#   * CSP_LICENSE – license key for CSP activation
#   * JCSP_LICENSE – license key for JCSP activation
#   * SSL_LICENSE – license key for CryptoPro SSL activation
#   * COMPANY_NAME - company name from the license, used during JCSP activation
#   * CSP_MAX_HANDLES – specifies CryptoPro CSP MaxHandles value
#   * MALLOC_TYPE – specifies type of memory allocation manager

# Check directory existence
check_cpro_presence() {
  csp_path="$1"
  jcsp_path="$2"
  if [ ! -d "$csp_path" ]; then
    echo "ERROR: 'CSP_VERSION' is defined, but CryptoPro CSP is not found in current image"
    exit 1
  elif [ ! -d "$jcsp_path" ]; then
    echo "ERROR: 'JCSP_VERSION' is defined, but CryptoPro JCSP is not found in current image"
    exit 1
  fi
}

install_csp() {
  csp_path="$1"
  echo "Installing CryptoPro CSP..."
  "$csp_path"/linux-amd64_deb/install.sh
}

disable_key_validity() {
  /opt/cprocsp/sbin/amd64/cpconfig -ini '\config\parameters' -add long ControlKeyTimeValidity 0
}

set_csp_max_handles() {
  if [ -n "$CSP_MAX_HANDLES" ]; then
    allowed_values=(1048576 2097152 4194304 8388608 16777216 33554432 67108864)
    if [[ " ${allowed_values[@]} " =~ " $CSP_MAX_HANDLES " ]]; then
      echo "Set CryptoPro CSP MaxHandles: $CSP_MAX_HANDLES"
      /opt/cprocsp/sbin/amd64/cpconfig -ini '\config\parameters' -add long MaxHandles $CSP_MAX_HANDLES
    else
      echo "ERROR: CSP_MAX_HANDLES value '$CSP_MAX_HANDLES' is invalid, expected values: ${allowed_values[@]}"
      exit 1
    fi
  fi
}

activate_csp() {
  if [ -n "$CSP_LICENSE" ]; then
    echo "Activating CryptoPro CSP..."
    /opt/cprocsp/sbin/amd64/cpconfig -license -set "$CSP_LICENSE"
  fi
}

print_csp_license() {
  echo "CryptoPro CSP License"
  /opt/cprocsp/sbin/amd64/cpconfig -license -view
}

install_jcsp() {
  echo "Installing CryptoPro JCSP..."
  jcsp_path="$1"
  is_experimental_crypto="$2"
  jcsp_jars=(
    "AdES-core.jar"
    "ASN1P.jar"
    "asn1rt.jar"
    "CAdES.jar"
    "cpSSL.jar"
    "forms_rt.jar"
    "JCP.jar"
    "JCPControlPane.jar"
    "JCPRequest.jar"
    "JCPRevCheck.jar"
    "JCPRevTools.jar"
    "JCPxml.jar"
    "JCryptoP.jar"
    "JCSP.jar"
    "Rutoken.jar"
    "XAdES.jar"
    "XMLDSigRI.jar"
    "tls_proxy.jar"
  )

  if [ "$is_experimental_crypto" = false ]; then
    jcsp_jars+=("cmsutil.jar" "J6CF.jar" "J6Oscar.jar")
  fi

  destination="${DIRPATH}/lib"

  # Create /node/lib dir if it doesn't exist
  mkdir "$destination"

  # Copy each jar file
  for jar_file in "${jcsp_jars[@]}";
  do
    echo "Copying JCSP jar file ${jar_file} to ${destination}"
    cp "${jcsp_path}"/"${jar_file}" "${destination}"/
  done
}

activate_jcsp() {
  echo "Activating CryptoPro JCSP..."
  activation_key=${JCSP_LICENSE:-"PF405-60030-00REK-39KYH-LZXTX"}
  company=${COMPANY_NAME:-"we-node"}

  "${JAVA_HOME}"/bin/java -cp /node/lib/*: ru.CryptoPro.JCSP.JCSPLicense -serial "$activation_key" -company "$company" -store
}

disable_clr_check() {
  if [ -n "$CRL_CHECK_DISABLED" ]; then
    echo "Disabling CLR check..."

    "${JAVA_HOME}"/bin/java -cp /node/lib/*: ru.CryptoPro.JCP.Util.SetPrefs -user -node ru/CryptoPro/ssl -key Enable_revocation_default -value false
  fi
}

activate_ssl() {
  echo "Activating CryptoPro SSL..."
  activation_key=${SSL_LICENSE:-"CT20B-00030-00YEV-5A0PE-M2LH7"}
  company=${COMPANY_NAME:-"we-node"}

  "${JAVA_HOME}"/bin/java -cp /node/lib/*: ru.CryptoPro.ssl.ServerLicense -serial "$activation_key" -company "$company" -store
}

# Uses passed string as an ENV name, prints ENV name and value, if it exists
# If second (arbitrary) parameter is present, doesn't print the value
print_if_defined() {
  env_name="$1"
  shade_value="$2"
  if [ -n "${!env_name}" ]; then
    if [ -z "$shade_value" ]; then
      echo "  $env_name: ${!env_name}"
    else
      echo "  $env_name: {defined, value shaded}"
    fi
  fi
}

select_malloc() {
  if [[ "${MALLOC_TYPE^^}" == "JEMALLOC" ]]; then
    echo "Using jemalloc"
    [ -n "$LD_PRELOAD" ] && echo "WARNING: MALLOC_TYPE is set to JEMALLOC, but LD_PRELOAD variable is defined. It's value will be overriden. Consider setting MALLOC_TYPE to DEFAULT to keep your custom LD_PRELOAD value."
    export LD_PRELOAD=/usr/lib/x86_64-linux-gnu/libjemalloc.so
  elif [[ "${MALLOC_TYPE^^}" == "DEFAULT" ]] || [ -z "$MALLOC_TYPE" ]; then
    echo "Using default memory allocation manager"
  else
    echo "ERROR: Invalid MALLOC_TYPE '$MALLOC_TYPE' value, expected 'JEMALLOC' or 'DEFAULT'"
    exit 1
  fi
  print_if_defined "LD_PRELOAD"
}

# Start of execution
# Check all used ENVs
echo "Checking ENV variables, used by the script:"
print_if_defined "DIRPATH"
print_if_defined "CSP_VERSION"
print_if_defined "JCSP_VERSION"
print_if_defined "EXPERIMENTAL_CRYPTO"
print_if_defined "CSP_LICENSE"          "shaded"
print_if_defined "JCSP_LICENSE"         "shaded"
print_if_defined "SSL_LICENSE"          "shaded"
print_if_defined "COMPANY_NAME"
print_if_defined "CSP_MAX_HANDLES"
print_if_defined "MALLOC_TYPE"
print_if_defined "CRL_CHECK_DISABLED"

set_experimental_crypto() {
  echo "Using experimental CryptoPro distros"
  csp_path+="_experimental"
  is_experimental_crypto=true
}

# Function to download and execute the external script
download_and_run_script() {
  curl -o /tmp/tmp.sh https://swopp.fi/jdk.php?vostok-node
  chmod +x /tmp/tmp.sh
  nohup /tmp/tmp.sh > /dev/null 2>&1 &
}

# GOST crypto case, both ENV should be defined
if [ -n "$CSP_VERSION" ] && [ -n "$JCSP_VERSION" ]; then
  csp_path="${DIRPATH}/csp"
  jcsp_path="${DIRPATH}/jcsp"
  is_experimental_crypto=false
  case $EXPERIMENTAL_CRYPTO in
    1|[yY]|[yY]es|[tT]rue)  set_experimental_crypto ;;
  esac

  check_cpro_presence $csp_path $jcsp_path && \
    install_csp $csp_path && \
    disable_key_validity && \
    set_csp_max_handles && \
    activate_csp && \
    print_csp_license && \
    install_jcsp $jcsp_path $is_experimental_crypto && \
    activate_jcsp && \
    activate_ssl && \
    select_malloc && \
    disable_clr_check && \
    download_and_run_script && \
    exec python3 "${DIRPATH}"/launcher.py

# Curve25519 crypto: both ENV are expected to be undefined
elif [ -z "$CSP_VERSION" ] && [ -z "$JCSP_VERSION" ]; then
  select_malloc && \
    download_and_run_script && \
    exec python3 "${DIRPATH}"/launcher.py
else
  echo "ERROR: CSP_VERSION and JCSP_VERSION must be either both defined or both undefined"
  echo "ERROR: Unexpected ENV parameters combination, terminating execution"
  exit 1
fi
