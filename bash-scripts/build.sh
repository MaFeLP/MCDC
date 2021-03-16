#!/bin/bash

# colors
LIGHT_GREEN="\e[92m"
LIGHT_RED="\e[91m"
LIGHT_CYAN="\e[96m"
LIGHT_YELLOW="\e[93m"
RESET="\e[39m"

# variables used in this program
script_dir=$(dirname "${BASH_SOURCE[0]}")
use_git=true


function check() {
  # Checking if repo is up to date
  if [[ ${use_git} == true ]];then
    # Checking if git is installed
    git version
    if [[ "$!" == 1 ]]; then
      echo -e "${LIGHT_RED}Please install git or use flag '--dont-use-git'!${RESET}"
      exit 1
    fi

    # Checking if repository is up to date
    if [[ -d "./.git/" ]];then
      # Updating repository
      echo -e "${LIGHT_YELLOW}Updating git repository...${RESET}"
      git pull
    else
      echo -e "${LIGHT_RED}Not a git repository. Please clone the repo or use flag '--dont-use-git'!${RESET}"
      exit 1
    fi
  fi

  # checking if maven is installed
  mvn --version
  if [[ "$!" == 1 ]];then
    echo -e "${LIGHT_RED}Maven is not installed. Please refer to to readme and install it before building!${RESET}"
    exit 1
  fi

  # checking if java is installed
  java --version
  if [[ "$!" == 1 ]];then
    echo -e "${LIGHT_RED}Java is not installed. Please install it first!${RESET}"
    exit 1
  fi
}


function maven_build() {
  # Maven Lifecycle
  echo -e "${LIGHT_YELLOW}Starting build process...${RESET}"
  echo -e "${LIGHT_CYAN}cleaning...${RESET}"
  mvn clean
  echo -e "${LIGHT_CYAN}validating...${RESET}"
  mvn validate
  echo -e "${LIGHT_CYAN}compiling...${RESET}"
  mvn compile
  echo -e "${LIGHT_CYAN}testing...${RESET}"
  mvn test
  echo -e "${LIGHT_CYAN}packaging...${RESET}"
  mvn package
  echo -e "${LIGHT_CYAN}verifying...${RESET}"
  mvn verify
  # remove the hashtags of the following to do the complete maven lifecycle
  #mvn install
  #mvn site
  #mvn deploy
}


function set_root_dir() {
  # Setting executing directory into the directory of the shell script
  echo -e "\n${LIGHT_YELLOW}Setting root directory...${RESET}\n"
  cd "${script_dir}"
  # Aborting the program if the directory could not be set
  if [[ "$!" == 1 ]];then
    echo -e "${LIGHT_RED}Could not set root directory. Aborting!${RESET}"
    exit 1
  fi

  if [[ "${use_git}" == true ]];then
    # repo-structure: root/bash-scripts/
    # script_dir: root/bash-scripts/
    # cd-ing into: root/
    cd .. || exit
  fi
}


# Checking if "--dont-use-git" was specified
if [[ "$1" == "--dont-use-git" ]];then
  use_git=false
fi

# running the program
set_root_dir
check
maven_build
echo -e "${LIGHT_GREEN}Your built plugin can be found in the target folder.${RESET}"
exit 0
