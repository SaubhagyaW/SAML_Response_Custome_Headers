# WSO2-IS
Send custom headers along with SAML Response to the Service Provider.

Edit <IS_HOME>/repository/resources/identity/pages/samlsso_response.html.
Make an Ajax call to a custom endpoint which processes the SAML Response and returns the necessary custom header values.
Make an Ajax call to the Service Provider containing the custom headers.
