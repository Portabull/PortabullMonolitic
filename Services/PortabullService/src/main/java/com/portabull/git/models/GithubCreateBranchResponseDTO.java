package com.portabull.git.models;

public class GithubCreateBranchResponseDTO {
    private String ref;
    private ObjectInfo object;

    public class ObjectInfo {
        private String sha;

        public String getSha() {
            return sha;
        }

        public void setSha(String sha) {
            this.sha = sha;
        }
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public ObjectInfo getObject() {
        return object;
    }

    public void setObject(ObjectInfo object) {
        this.object = object;
    }
}