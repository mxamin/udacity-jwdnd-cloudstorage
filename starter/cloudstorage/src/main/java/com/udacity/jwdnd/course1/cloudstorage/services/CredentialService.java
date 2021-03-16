package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CredentialService {
    private final CredentialMapper credentialMapper;

    public CredentialService(CredentialMapper credentialMapper) {
        this.credentialMapper = credentialMapper;
    }

    public List<Credential> getCredentials(Integer userId) {
        return this.credentialMapper.getCredentials(userId);
    }

    public Credential getCredential(Integer credentialId) { return this.credentialMapper.getCredential(credentialId); }

    public Integer addCredential(Credential credential) {
        return this.credentialMapper.addCredential(credential);
    }

    public void updateCredential(Credential credential) {
        this.credentialMapper.updateCredential(credential);
    }

    public void deleteCredential(Integer credentialId, Integer userId) {
        this.credentialMapper.deleteCredential(credentialId, userId);
    }
}
