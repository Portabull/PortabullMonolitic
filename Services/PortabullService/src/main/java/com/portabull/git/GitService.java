package com.portabull.git;

import com.portabull.git.models.GitConfigDTO;
import com.portabull.git.models.GitDiffResponse;
import com.portabull.git.models.GitDownloadResponse;

public interface GitService {

    GitDownloadResponse downloadRepository(GitConfigDTO gitConfigDTO);

    String getCurrentCommitSha(GitConfigDTO gitConfigDTO);

    GitDiffResponse getGitDiffernces(GitConfigDTO gitConfigDTO, String fromCommitSha);

}
