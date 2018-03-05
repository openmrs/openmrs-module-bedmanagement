import React from 'react';
import axios from 'axios';
import PropTypes from 'prop-types';
import {Cookies} from 'react-cookie';
import {FormattedMessage} from 'react-intl';

import UrlHelper from 'utilities/urlHelper';
import LocaleHelper from 'utilities/localeHelper';
import messages from 'i18n/messages';

export default class LocaleList extends React.Component {
    constructor(props, context) {
        super(props, context);

        this.urlHelper = new UrlHelper();
        this.localeHelper = new LocaleHelper();
        this.cookies = new Cookies();
        this.store = this.context.store;
        this.setLanguage = this.setLanguage.bind(this);
    }

    async setLanguage(locale) {
        await axios.post(this.urlHelper.apiBaseUrl() + '/session', {locale: locale});
        const localeCode = this.localeHelper.getLocaleCode(locale);
        this.cookies.set('__openmrs_language', locale, {path: '/'});
        this.store.setState({
            localeCode: localeCode,
            messages: typeof messages[localeCode] != 'undefined' ? messages[localeCode] : messages['en']
        });
    }

    render() {
        return (
            <div>
                <small className="language-list">
                    <FormattedMessage id="CHOOSE_LANGUAGE" />: &nbsp;
                    {this.props.allowedLocales.map((locale, index) => (
                        <span key={locale}>
                            {index != 0 ? ' | ' : ''}
                            <a
                                href="javascript:void(0)"
                                onClick={() => this.setLanguage(locale)}
                                className={
                                    this.props.localeCode == this.localeHelper.getLocaleCode(locale)
                                        ? 'active locale'
                                        : 'locale'
                                }>
                                {this.localeHelper.getNativeNameByLocaleCode(locale)}
                            </a>
                        </span>
                    ))}
                </small>
            </div>
        );
    }
}

LocaleList.propTypes = {
    allowedLocales: PropTypes.array.isRequired,
    localeCode: PropTypes.string.isRequired
};

LocaleList.contextTypes = {
    store: PropTypes.object,
    intl: PropTypes.object
};
