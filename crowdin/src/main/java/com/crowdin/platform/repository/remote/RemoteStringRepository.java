package com.crowdin.platform.repository.remote;

import com.crowdin.platform.api.CrowdinApi;

public class RemoteStringRepository {

    private final CrowdinApi crowdinApi;

    public RemoteStringRepository(CrowdinApi crowdinApi) {
        this.crowdinApi = crowdinApi;
    }
}
