package com.portabull.git;

import com.portabull.git.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

@Service
public class GitHubService implements GitService {

    static Logger log = LoggerFactory.getLogger(GitHubService.class);

    RestTemplate errorHandlerRT = new RestTemplate();
    public final String pullDifferences = "/repos/{owner}/{repo}/compare/{from_to}";

    public final String reference = "/repos/{owner}/{repo}/git/refs/heads/{branch}";
    public final String downloadRepository = "/repos/{owner}/{repo}/zipball/{commit_sha}";

    @Override
    public GitDownloadResponse downloadRepository(GitConfigDTO gitConfigDTO) {

        try {
            GitDownloadResponse response = new GitDownloadResponse();

            response.setFile(File.createTempFile(gitConfigDTO.getBranch(), ".zip"));


            response.setLatestCommitSha(getCurrentCommitSha(gitConfigDTO));

            try (FileOutputStream fileOutputStream = new FileOutputStream(response.getFile(), true)) {
                errorHandlerRT.execute(
                        gitConfigDTO.getProviderURL() + downloadRepository,
                        HttpMethod.GET,
                        clientHttpRequest -> GitUtil.prepareAuthHeaders(gitConfigDTO, clientHttpRequest.getHeaders()),
                        clientHttpResponse -> {
                            StreamUtils.copy(clientHttpResponse.getBody(), fileOutputStream);
                            return response.getFile();
                        },
                        gitConfigDTO.getOwner(),
                        gitConfigDTO.getRepository(),
                        response.getLatestCommitSha()
                );
            }

            return response;

        } catch (Exception e) {
            log.error("Error occured while downloading the git repo : {}", e);
            GitDownloadResponse response = new GitDownloadResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(500);
            response.setStatus("");
            return response;
        }

    }


    public String getCurrentCommitSha(GitConfigDTO gitConfigDTO) {
        try {

            HttpEntity<String> request = GitUtil.createAuthHeader(gitConfigDTO, Optional.empty());
            ResponseEntity<GithubCreateBranchResponseDTO> currentCommitResponse = errorHandlerRT.exchange(
                    gitConfigDTO.getProviderURL() + reference, HttpMethod.GET, request,
                    GithubCreateBranchResponseDTO.class, gitConfigDTO.getOwner(), gitConfigDTO.getRepository(),
                    gitConfigDTO.getBranch());

            if (currentCommitResponse.getStatusCode().value() == 200) {
                return currentCommitResponse.getBody().getObject().getSha();
            } else {
                /* handling other error message */
//                GitUtil.handleError(currentCommitResponse.getStatusCode().value());
            }
        } catch (Exception e) {
            log.error("Error occurred in getBranchCommitSha for branch :{} and error  : {}", gitConfigDTO.getBranch(),
                    e);
        }
        return null;
    }

    @Override
    public GitDiffResponse getGitDiffernces(GitConfigDTO gitConfigDTO, String fromCommitSha) {

        GitDiffResponse response = new GitDiffResponse();
        response.setLatestCommitSha(fromCommitSha);
        response.setDiffs(new ArrayList<>());

        try {

            ResponseEntity<LinkedHashMap<String, Object>> diffResponse = errorHandlerRT.exchange(
                    gitConfigDTO.getProviderURL() + pullDifferences, HttpMethod.GET,
                    GitUtil.createAuthHeader(gitConfigDTO, Optional.empty()),
                    new ParameterizedTypeReference<LinkedHashMap<String, Object>>() {
                    }, gitConfigDTO.getOwner(), gitConfigDTO.getRepository(),
                    fromCommitSha + "..." + gitConfigDTO.getBranch());

            if (diffResponse.getStatusCode().value() != 200) {
                response.setMessage(
                        diffResponse.getBody().get("message") != null ? diffResponse.getBody().get("message").toString()
                                : null);
                response.setStatusCode(diffResponse.getStatusCode().value());
                return response;
            }

            List<Map<String, Object>> files = (List<Map<String, Object>>) diffResponse.getBody().get("files");
            List<Map<String, Object>> commits = (List<Map<String, Object>>) diffResponse.getBody().get("commits");
            response.setNoOfCommitsBehind(commits.size() >= 100 ? "99+" : commits.size() + "");
            if (!CollectionUtils.isEmpty(files)) {
                if (files.size() == 300) {
                    response.setStatusCode(413);
                    response.setStatus("FAILED");
                    response.setMessage("Limit Exceeded");
                    return response;
                }

                files.forEach(file -> response.getDiffs()
                        .add(createFile(file)
                        ));
                response.setLatestCommitSha(commits.get(commits.size() - 1).get("sha").toString());
                response.setDiffCount(response.getDiffs().size());
            } else {
                response.setMessage("Nothing to update - everything up to date");
            }
        } catch (Exception e) {
            log.error("Error occured while getting the differnces : {}", e);
            response.setMessage(e.getMessage());
            response.setStatusCode(500);
            response.setStatus("FAILED");
        }

        return response;
    }

    private GitDifferences createFile(Map<String, Object> file) {
        GitDifferences differences = new GitDifferences();
        differences.setNewFilePath(file.get("filename").toString());
        differences.setOldFilePath(
                file.get("previous_filename") != null ? file.get("previous_filename").toString() : null);

        differences.setStatus(GitUtil.getStatus(file.get("status").toString()));
        return differences;

    }


}
