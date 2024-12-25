package com.portabull.git;

import com.portabull.git.models.GitConfigDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class GitUtil {


    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String BASIC = "Basic ";
    public static final String PRIVATE_TOKEN = "Private-Token";

    public static HttpEntity<String> createAuthHeader(GitConfigDTO gitConfigDTO, Optional<Map<String, String>> additionalHeadersOptional) {
        if (null != gitConfigDTO.getProvider() && null != gitConfigDTO.getAccessToken()) {
            String authHeaderName = null;
            String authHeaderValue = null;
            if (gitConfigDTO.getProvider().equals(GitConfigDTO.GitProviderType.GITHUB)) {
                authHeaderName = AUTHORIZATION;
                authHeaderValue = BEARER + gitConfigDTO.getAccessToken();
            }
            /* for bit bucket provider we need user name */
            else if (gitConfigDTO.getProvider().equals(GitConfigDTO.GitProviderType.BITBUCKET_CLOUD)) {
                authHeaderName = AUTHORIZATION;
                authHeaderValue = BASIC + Base64.getEncoder().encodeToString((gitConfigDTO.getUserName() + ':' + gitConfigDTO.getAccessToken()).getBytes());
            } else if (gitConfigDTO.getProvider().equals(GitConfigDTO.GitProviderType.GITLAB)) {
                authHeaderName = PRIVATE_TOKEN;
                authHeaderValue = gitConfigDTO.getAccessToken();
            } else if (gitConfigDTO.getProvider().equals(GitConfigDTO.GitProviderType.AZURE_DEVOPS_REPO)) {
                authHeaderName = AUTHORIZATION;
                authHeaderValue = BASIC + Base64.getEncoder().encodeToString((':' + gitConfigDTO.getAccessToken()).getBytes());
            }

            HttpHeaders headers = new HttpHeaders();

            headers.add(authHeaderName, authHeaderValue);

            if (additionalHeadersOptional.isPresent()) {
                Map<String, String> additionalHeaders = additionalHeadersOptional.get();

                for (Map.Entry<String, String> additionalHeader : additionalHeaders.entrySet()) {
                    headers.add(additionalHeader.getKey(), additionalHeader.getValue());
                }
            }

            return new HttpEntity<>(headers);

        }

        return null;
    }


    public static void prepareAuthHeaders(GitConfigDTO gitConfigDTO, HttpHeaders headers) {

        if (gitConfigDTO.getProvider().equals(GitConfigDTO.GitProviderType.BITBUCKET_CLOUD)) {
            headers.set(AUTHORIZATION, BASIC + Base64.getEncoder()
                    .encodeToString((gitConfigDTO.getUserName() + ':' + gitConfigDTO.getAccessToken()).getBytes()));
        } else if (gitConfigDTO.getProvider().equals(GitConfigDTO.GitProviderType.GITHUB)) {
            headers.set(AUTHORIZATION, BEARER + gitConfigDTO.getAccessToken());
        } else if (gitConfigDTO.getProvider().equals(GitConfigDTO.GitProviderType.GITLAB)) {
            headers.set(PRIVATE_TOKEN, gitConfigDTO.getAccessToken());
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        } else if (gitConfigDTO.getProvider().equals(GitConfigDTO.GitProviderType.AZURE_DEVOPS_REPO)) {
            headers.set(AUTHORIZATION,
                    BASIC + Base64.getEncoder().encodeToString((':' + gitConfigDTO.getAccessToken()).getBytes()));
        }

    }


//    public void handleError(int statusCodeValue) throws GreatDataException {
//        switch (statusCodeValue) {
//            case 203, 401, 403:
//                throw new GreatDataException(ErrorConstants.UNAUTHORIZED_CODE, ErrorConstants.GDP401000_MESSAGE,
//                        ErrorConstants.GDP500705_DETAILS);
//            case 404:
//                throw new GreatDataException(ErrorConstants.NOT_FOUND_ERROR_CODE, ErrorConstants.GDP404000_MESSAGE,
//                        ErrorConstants.GDP500729_DETAILS);
//            default:
//                throw new GreatDataException(ErrorConstants.INTERNAL_SERVER_ERROR_CODE, ErrorConstants.GDP500000_MESSAGE,
//                        ErrorConstants.GDP500000_MESSAGE);
//        }
//    }

    public static String getStatus(String status) {
        switch (status) {
            case "added":
            case "add":
                return GitCommitChangeType.CREATE.name();
            case "removed":
            case "delete":
                return GitCommitChangeType.DELETE.name();
            case "renamed":
            case "rename":
            case "edit, rename":
                return GitCommitChangeType.MOVE.name();
            case "modified":
            case "edit":
                return GitCommitChangeType.UPDATE.name();
            default:
                return status;
        }
    }


    public enum GitCommitChangeType {
        CREATE,
        UPDATE,
        DELETE,
        MOVE,
        CHMOD
    }


}
