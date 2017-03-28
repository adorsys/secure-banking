#!/bin/bash
PROJECT_VERSION="1.0.0"

# Is this not a build which was triggered by setting a new tag?
if [ -z "$TRAVIS_TAG" ]; then
    echo -e "Starting to tag commit.\n"

    git config --global user.email "travis@travis-ci.org"
    git config --global user.name "Travis"
    git tag -a v${PROJECT_VERSION} -m "Travis build $PROJECT_VERSION pushed a tag."
    git push origin --tags
    git fetch origin
    echo -e $PROJECT_VERSION
    echo -e "Done magic with tags.\n"
fi