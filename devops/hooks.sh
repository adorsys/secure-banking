#!/bin/bash
set -e

SCRIPT_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

function formatReleaseTag {
    echo "v$1"
}

function formatSnapshotVersion {
    echo "$1-SNAPSHOT"
}

function set_services_version {
    cd $SCRIPT_PATH/.. && mvn -B versions:set -DnewVersion="$1"
}

function build_services {
    mvn clean install
}

function release_services {
    build_services
    set_services_version $1
}

function release_modules {
    release_services $1
}

function set_modules_version {
    set_services_version $1
}
