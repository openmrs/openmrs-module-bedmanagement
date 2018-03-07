import React from 'react';
import {Switch, Route} from 'react-router-dom';
import PropTypes from 'prop-types';
import {IntlProvider, FormattedMessage} from 'react-intl';

import AdmissionLocationWrapper from 'components/admissionLocation/admissionLocationWrapper';
import BedTypeWrapper from 'components/bedType/bedTypeWrapper';
import BedTagWrapper from 'components/bedTag/bedTagWrapper';
import LocaleList from 'components/locale/localeList';
import StateApi from 'utilities/stateApi';
import UrlHelper from 'utilities/urlHelper';
import messages from 'i18n/messages';

const urlHelper = new UrlHelper();
require('./app.css');
require('babel-polyfill');
class App extends React.Component {
    static childContextTypes = {
        store: PropTypes.object
    };

    getChildContext() {
        return {
            store: new StateApi(this)
        };
    }

    constructor(props) {
        super(props);
        this.state = {
            localeCode: props.localeCode,
            allowedLocales: props.allowedLocales,
            messages:
                typeof messages[props.localeCode] !== 'undefined'
                    ? messages[props.localeCode]
                    : messages[props.defaultLocale]
        };
    }

    render() {
        return (
            <IntlProvider locale={this.state.localeCode} messages={this.state.messages}>
                <div>
                    <div className="openmrs-header">
                        <span className="logo">
                            <img src="img/openmrs.png" alt="logo" />
                        </span>
                        <a href={urlHelper.originPath() + '/openmrs/logout'} className="logout">
                            <FormattedMessage id="LOGOUT" /> <i className="fa fa-sign-out" aria-hidden="true" />
                        </a>
                    </div>
                    <Switch>
                        <Route
                            path={urlHelper.owaPath() + '/admissionLocations.html'}
                            component={AdmissionLocationWrapper}
                        />
                        <Route path={urlHelper.owaPath() + '/bedTypes.html'} component={BedTypeWrapper} />
                        <Route path={urlHelper.owaPath() + '/bedTags.html'} component={BedTagWrapper} />
                    </Switch>
                    <LocaleList allowedLocales={this.state.allowedLocales} localeCode={this.state.localeCode} />
                </div>
            </IntlProvider>
        );
    }
}

App.propTypes = {
    localeCode: PropTypes.string.isRequired,
    defaultLocale: PropTypes.string.isRequired,
    allowedLocales: PropTypes.array.isRequired
};

export default App;
