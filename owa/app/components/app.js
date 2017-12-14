import React from 'react';
import {Switch, Route} from 'react-router-dom';
import PropTypes from 'prop-types';

import AdmissionLocationWrapper from 'components/admissionLocation/admissionLocationWrapper';
import BedTypeWrapper from 'components/bedType/bedTypeWrapper';
import BedTagWrapper from 'components/bedTag/bedTagWrapper';
import StateApi from 'utilities/stateApi';
import UrlHelper from 'utilities/urlHelper';

const urlHelper = new UrlHelper();
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

    style = {
        boxContainer : {
            width: 1240,
            margin: '0 auto',
            height: '100%'
        }
    };

    render() {
        return <div style={this.style.boxContainer}>
            <Switch>
                <Route path={urlHelper.owaPath() +'/admissionLocations.html'} component={AdmissionLocationWrapper}/>
                <Route path={urlHelper.owaPath() +'/bedTypes.html'} component={BedTypeWrapper}/>
                <Route path={urlHelper.owaPath() +'/bedTags.html'} component={BedTagWrapper}/>
            </Switch>
        </div>;
    }
}

export default App;