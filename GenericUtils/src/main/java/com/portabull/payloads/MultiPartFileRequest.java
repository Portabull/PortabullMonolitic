package com.portabull.payloads;

import javax.activation.MimetypesFileTypeMap;
import java.util.ArrayList;
import java.util.List;

public class MultiPartFileRequest {

    private List<MultiPartRequest> multiPartRequests;

    public List<MultiPartRequest> getMultiPartRequests() {
        return multiPartRequests;
    }

    public void setMultiPartRequests(List<MultiPartRequest> multiPartRequests) {
        this.multiPartRequests = multiPartRequests;
    }

    public void setMultiPartRequest(MultiPartRequest multiPartRequest) {
        if (this.multiPartRequests == null) {
            this.multiPartRequests = new ArrayList<>();
        }

        this.multiPartRequests.add(multiPartRequest);
    }

    public MultiPartFileRequest getMultiPartFileRequest(byte[] fileBytes, String fileNameWithExtension, String parameterName, String type) {
        MultiPartRequest multiPartRequest = new MultiPartRequest();
        multiPartRequest.setBytes(fileBytes);
        multiPartRequest.setFileNameWithExtension(fileNameWithExtension);
        multiPartRequest.setParameterName(parameterName);
        multiPartRequest.setType(type);
        this.setMultiPartRequest(multiPartRequest);
        multiPartRequest.setContentType(getContentType(fileNameWithExtension));
        return this;
    }

    public String getContentType(final String fileName) {
        final MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
        return fileTypeMap.getContentType(fileName);
    }


}
