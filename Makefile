.DEFAULT_GOAL := help
.PHONY: help

help: ## Show this help
	@egrep -h '\s##\s' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

build: ## Create a production release of the code
	npm run release

push: ## Push the most recent version of the code to GCP Cloud Run
	gcloud run deploy metadata --allow-unauthenticated --source .

deploy: build ## Create release and have GCP build and deploy my container
	gcloud run deploy metadata --allow-unauthenticated --source .

log: ## Tail the logs from GCP Stackdriver
	gcloud alpha logging tail

run: ## Run program via Node interpreter
	node main.js

test: ## test
	echo "test"

local: ## local
	echo "should explore local testing"

delete: ## Delete Cloud Run service
	gcloud run servies delete metadata --region us-central1
