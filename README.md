# cljs-metadata-server
A program which queries the local metadata server
fpr GCP Cloud Run

More info on which data can be queried is located here:
https://cloud.google.com/run/docs/reference/container-contract#sandbox

# Build and Deploy to Cloud Run
gcloud run deploy metadata --allow-unauthenticated --source .


## NOTE
The target directory was not being copied over via Google Build.
It dynamically builds a .gcloudignore file, and the target file was in the list.
See: https://cloud.google.com/sdk/gcloud/reference/topic/gcloudignore
Useful command: gcloud meta list-files-for-upload
