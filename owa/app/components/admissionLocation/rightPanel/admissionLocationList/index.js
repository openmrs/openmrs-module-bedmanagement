import React from 'react';
import PropTypes from 'prop-types';

import Breadcrumb from 'components/admissionLocation/rightPanel/breadcrumb';
import LocationBlock from 'components/admissionLocation/rightPanel/locationBlock';
import BedLayout from 'components/admissionLocation/rightPanel/bedLayout';
import AdmissionLocationHelper from 'utilities/admissionLocationHelper';

require('./admissionLocationList.css');
export default class AdmissionLocationList extends React.Component {
    constructor(props) {
        super(props);

        this.admissionLocationHelper = new AdmissionLocationHelper();
        this.childAdmissionLocations = this.admissionLocationHelper.getChildAdmissionLocations(
            props.admissionLocationFunctions.getAdmissionLocations(),
            props.activeUuid
        );
        this.addWardClickHandler = this.addWardClickHandler.bind(this);
        this.getBody = this.getBody.bind(this);
    }

    componentWillUpdate(nextProps, nextState) {
        this.childAdmissionLocations = this.admissionLocationHelper.getChildAdmissionLocations(
            this.props.admissionLocationFunctions.getAdmissionLocations(),
            nextProps.activeUuid
        );
    }

    addWardClickHandler() {
        this.props.admissionLocationFunctions.setState({
            activePage: 'addEditLocation',
            pageData: {
                operation: 'add'
            },
            activeUuid: this.props.activeUuid
        });
    }

    getBody() {
        if (Object.keys(this.childAdmissionLocations).length == 0 && this.props.activeUuid == null) {
            return (
                <span className="btn btn-primary" onClick={this.addWardClickHandler}>
                    <i className="icon fa fa-plus" aria-hidden="true" /> Add Ward
                </span>
            );
        } else if (Object.keys(this.childAdmissionLocations).length == 0) {
            return (
                <BedLayout
                    activeUuid={this.props.activeUuid}
                    admissionLocationFunctions={this.props.admissionLocationFunctions}
                />
            );
        } else {
            return (
                <div>
                    {Object.keys(this.childAdmissionLocations).map((key) => (
                        <LocationBlock
                            key={key}
                            admissionLocation={this.props.admissionLocationFunctions.getAdmissionLocationByUuid(key)}
                            admissionLocationFunctions={this.props.admissionLocationFunctions}
                        />
                    ))}
                    <span className="btn btn-primary" onClick={this.addWardClickHandler}>
                        <i className="icon fa fa-plus" aria-hidden="true" /> Add Ward
                    </span>
                </div>
            );
        }
    }

    render() {
        return (
            <div className="main-container">
                <Breadcrumb
                    activeUuid={this.props.activeUuid}
                    admissionLocationFunctions={this.props.admissionLocationFunctions}
                />
                <div className="main-block">{this.getBody()}</div>
            </div>
        );
    }
}

AdmissionLocationList.contextTypes = {
    store: PropTypes.object,
    intl: PropTypes.object
};

AdmissionLocationList.propTypes = {
    activeUuid: PropTypes.string,
    admissionLocationFunctions: PropTypes.object.isRequired
};
