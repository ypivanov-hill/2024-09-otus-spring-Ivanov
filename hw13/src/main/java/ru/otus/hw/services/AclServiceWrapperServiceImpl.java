package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@RequiredArgsConstructor
@Service
public class AclServiceWrapperServiceImpl implements AclServiceWrapperService  {

    private final MutableAclService mutableAclService;

    @Override
    public void createPermission(Object object, Permission permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Sid owner = new PrincipalSid(authentication);
        ObjectIdentity oid = new ObjectIdentityImpl(object);

        MutableAcl acl = getMutableAcl(oid);
        acl.insertAce(acl.getEntries().size(), permission, owner, Boolean.TRUE);
        mutableAclService.updateAcl(acl);

        createAdminPermission(oid);
    }

    private MutableAcl getMutableAcl(ObjectIdentity oid) {
        MutableAcl acl;
        try {
             acl = (MutableAcl) mutableAclService.readAclById(oid);
        } catch (org.springframework.security.acls.model.NotFoundException e) {

            acl = mutableAclService.createAcl(oid);
        }
        return acl;
    }

    @Override
    public void deletePermission(String type, Serializable identifier) {
        ObjectIdentity oid = new ObjectIdentityImpl(type, identifier);
        mutableAclService.deleteAcl(oid, Boolean.FALSE);
    }

    @Override
    public void createAllPermission(Object object) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Sid owner = new PrincipalSid(authentication);
        ObjectIdentity oid = new ObjectIdentityImpl(object);

        MutableAcl acl = getMutableAcl(oid);
        acl.insertAce(acl.getEntries().size(), BasePermission.READ, owner, Boolean.TRUE);
        acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, owner, Boolean.TRUE);
        acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, owner, Boolean.TRUE);
        mutableAclService.updateAcl(acl);

        createAdminPermission(oid);
    }

    private void createAdminPermission(ObjectIdentity oid) {
        Sid admin = new GrantedAuthoritySid("ROLE_ADMIN");
        MutableAcl acl = getMutableAcl(oid);
        acl.insertAce(acl.getEntries().size(), BasePermission.READ, admin, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, admin, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, admin, true);
        mutableAclService.updateAcl(acl);
    }

}
