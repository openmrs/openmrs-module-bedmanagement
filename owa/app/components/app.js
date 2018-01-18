import React from 'react';
import {Switch, Route} from 'react-router-dom';
import PropTypes from 'prop-types';

import AdmissionLocationWrapper from 'components/admissionLocation/admissionLocationWrapper';
import BedTypeWrapper from 'components/bedType/bedTypeWrapper';
import BedTagWrapper from 'components/bedTag/bedTagWrapper';
import StateApi from 'utilities/stateApi';
import UrlHelper from 'utilities/urlHelper';

const urlHelper = new UrlHelper();
require('./app.css');
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
            timeStamp: new Date()
        };
    }

    render() {
        return (
            <div>
                <div className="openmrs-header">
                    <span className="logo">
                        <img src="img/openmrs.png" alt="logo" />
                    </span>
                    <a href={urlHelper.originPath() + '/openmrs/logout'} className="logout">
                        Logout <i className="fa fa-sign-out" aria-hidden="true" />
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
                <div>
                    <small>
                        {' '}
                        Choose language: <a href="#">English</a>, <a href="#">Spanish</a>{' '}
                    </small>
                </div>
            </div>
        );
    }
}

export default App;
