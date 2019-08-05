package org.georchestra.console.ws.backoffice.users;

import org.georchestra.console.dao.AdvancedDelegationDao;
import org.georchestra.console.ds.AccountDao;
import org.georchestra.console.ds.AccountDaoImpl;
import org.georchestra.console.dto.AccountImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class UsersExportTest {

    private UsersExport us;

    @Before
    public void setUp() throws Exception {
        AccountDao mockedDao = mock(AccountDao.class);
        AccountImpl a = new AccountImpl();
        a.setCommonName("Pierre");
        a.setSurname("Mauduit");
        a.setEmail("abc@example.com");

        Authentication auth = mock(Authentication.class);
        Collection<GrantedAuthority> authorities = Collections.singleton(AdvancedDelegationDao.ROLE_SUPERUSER);
        doReturn(authorities).when(auth).getAuthorities();
        SecurityContextHolder.getContext().setAuthentication(auth);

        Mockito.when(mockedDao.findByUID(Mockito.anyString())).thenReturn(a);
        UserInfoExporter exporter = new UserInfoExporterImpl(mockedDao);
        us = new UsersExport(exporter);
    }

    @Test
    public void testGetUsersAsCsv() throws Exception {
        String s = us.getUsersAsCsv("[\"pmauduit\"]");

        String headers = "First Name,Middle Name,Last Name,Title,Suffix,Initials,Web Page,Gender,Birthday,Anniversary,"
                + "Location,Language,Internet Free Busy,Notes,E-mail Address,E-mail 2 Address,E-mail 3 Address,Primary Phone,Home Phone,"
                + "Home Phone 2,Mobile Phone,Pager,Home Fax,Home Address,Home Street,Home Street 2,Home Street 3,Home Address PO Box,Home City,"
                + "Home State,Home Postal Code,Home Country,Spouse,Children,Manager's Name,Assistant's Name,Referred By,Company Main Phone,"
                + "Business Phone,Business Phone 2,Business Fax,Assistant's Phone,Company,Job Title,Department,Office Location,Organizational ID Number,"
                + "Profession,Account,Business Address,Business Street,Business Street 2,Business Street 3,Business Address PO Box,Business City,"
                + "Business State,Business Postal Code,Business Country,Other Phone,Other Fax,Other Address,Other Street,Other Street 2,Other Street 3,"
                + "Other Address PO Box,Other City,Other State,Other Postal Code,Other Country,Callback,Car Phone,ISDN,Radio Phone,TTY/TDD Phone,Telex,"
                + "User 1,User 2,User 3,User 4,Keywords,Mileage,Hobby,Billing Information,Directory Server,Sensitivity,Priority,Private,Categories";
        String res = "Pierre,,Mauduit,,,,,,,,,,,,abc@example.com,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,," +
                ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,";
        String[] splitted = s.split("\r\n");

        assertFalse("The CSV contains \"null\", unexpected", s.contains("null"));
        assertTrue("The CSV should contain \"abc@example.com\"", s.contains("abc@example.com"));
        assertEquals("The CSV should have the headers", splitted[0], headers);
        assertEquals("The CSV payoad should match entry data", splitted[1], res);
    }

    @Test
    public void testGetUsersAsVcf() throws Exception {
        String s = us.getUsersAsVcard("[\"pmauduit\"]");

        assertTrue("expected ret containing BEGIN:VCARD, not found",
                s.startsWith("BEGIN:VCARD"));
        assertTrue("Expect vcard version to be 3", s.contains("VERSION:3.0"));
    }

    // TODO: these tests are never being run, refactor them as proper integration
    // tests
    private void setUpAgainstRealLdap() {
        assumeTrue(System.getProperty("console.test.openldap.ldapurl") != null
                && System.getProperty("console.test.openldap.basedn") != null);

        String ldapUrl = System.getProperty("console.test.openldap.ldapurl");
        String baseDn = System.getProperty("console.test.openldap.basedn");

        DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(ldapUrl + baseDn);
        contextSource.setBase(baseDn);
        contextSource.setUrl(ldapUrl);
        contextSource.setBaseEnvironmentProperties(new HashMap<String, Object>());
        contextSource.setAnonymousReadOnly(true);
        contextSource.setCacheEnvironmentProperties(false);

        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);

        AccountDaoImpl adao = new AccountDaoImpl(ldapTemplate);
        adao.setUserSearchBaseDN("ou=users");

//        us.setAccountDao(adao);
    }

    @Test
    public void testGetUsersAsVcfAgainstOpenLdap() throws Exception {
        setUpAgainstRealLdap();
        String vcf = us.getUsersAsVcard("[\"testadmin\", \"testuser\"]");
        assertTrue("VCARD should contain both email address for testadmin and testuser",
                vcf.contains("psc+testuser@georchestra.org") && vcf.contains("psc+testadmin@georchestra.org"));
    }

    @Test
    public void testGetUsersAsCsvAgainstOpenLdap() throws Exception {
        setUpAgainstRealLdap();
        String csv = us.getUsersAsVcard("[\"testadmin\", \"testuser\"]");

        assertTrue("CSV should contain both email address for testadmin and testuser",
                csv.contains("psc+testuser@georchestra.org") && csv.contains("psc+testadmin@georchestra.org"));
        assertEquals("CSV should contain 3 lines", 3, csv.split("\r\n").length);
    }

}
