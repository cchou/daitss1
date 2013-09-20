export RUBYLIB="$(pwd)/lib:$RUBYLIB"

if [[ "$HOSTNAME" == "retsina.fcla.edu" ]] ;then
    specific_config="$(pwd)/config/stage.yml"
    export DAITSS_HOME=/stage/opt/daitss 
else
    specific_config=$(pwd)/config/$USER.yml
    if [[ "$USER" == "franco" ]] ;then
	export DAITSS_HOME=$HOME/Code/daitss
    else
	export DAITSS_HOME=$HOME/Workspace/daitss
    fi
fi

export DAITSS_TEST_CONFIG="$specific_config:$(pwd)/config/config.yml"
export PATH="$DAITSS_HOME/bin:$(pwd)/mock_env/bin:$PATH"
export RUBYOPT=rubygems
