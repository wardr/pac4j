package org.pac4j.oauth.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.orcid.OrcidProfile;
import org.pac4j.oauth.profile.orcid.OrcidProfileDefinition;
import org.pac4j.scribe.builder.api.OrcidApi20;

/**
 * <p>This class is the OAuth client to authenticate users in ORCiD.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.orcid.OrcidProfile}.</p>
 * <p>More information at http://support.orcid.org/knowledgebase/articles/175079-tutorial-retrieve-data-from-an-orcid-record-with</p>
 *
 * @author Jens Tinglev
 * @since 1.6.0
 */
public class OrcidClient extends OAuth20Client<OrcidProfile> {

    protected static final String DEFAULT_SCOPE = "/orcid-profile/read-limited";

    protected String scope = DEFAULT_SCOPE;

    public OrcidClient() {
    }

    public OrcidClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void internalInit(final WebContext context) {
        configuration.setApi(new OrcidApi20());
        configuration.setProfileDefinition(new OrcidProfileDefinition());
        configuration.setScope(this.scope);
        configuration.setHasGrantType(true);
        configuration.setTokenAsHeader(true);
        configuration.setHasBeenCancelledFactory(ctx -> {
            final String error = ctx.getRequestParameter(OAuthCredentialsException.ERROR);
            final String errorDescription = ctx.getRequestParameter(OAuthCredentialsException.ERROR_DESCRIPTION);
            // user has denied permissions
            if ("access_denied".equals(error) && "User denied access".equals(errorDescription)) {
                return true;
            } else {
                return false;
            }
        });
        setConfiguration(configuration);

        super.internalInit(context);
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }
}
