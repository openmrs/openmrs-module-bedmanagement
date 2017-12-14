import React from 'react';
import PropTypes from 'prop-types';

import AdmissionLocationHelper from 'utilities/admissionLocationHelper';

require('./breadcrumb.css');
export default class Breadcrumb extends React.PureComponent {
    constructor() {
        super();

        this.admissionLocationHelper = new AdmissionLocationHelper();
        this.breadcrumbLocations = this.breadcrumbLocations.bind(this);
        this.clickHandler = this.clickHandler.bind(this);
    }

    breadcrumbLocations(){
        return this.admissionLocationHelper.navigateUpToHigherLevel(
            this.props.admissionLocationFunctions.getAdmissionLocations(), [], this.props.activeUuid);
    }

    clickHandler(event, locationUuid){
        event.preventDefault();
        this.props.admissionLocationFunctions.setActiveLocationUuid(locationUuid);
    }

    render() {
        return <ul className="breadcrumb-section">
            <li><a href="javascript:void(0)" onClick={(e) => this.clickHandler(e, null)} title="Admission Locations">Admission Locations</a></li>
            {this.breadcrumbLocations().map(
                (admissionLocation, key) => <li key={key}>
                    <a href="javascript:void(0)" onClick={(e) => this.clickHandler(e, admissionLocation.uuid)} title={admissionLocation.name}>{admissionLocation.name}</a>
                </li>)}
        </ul>;
    }
}

Breadcrumb.propTypes = {
    activeUuid: PropTypes.string,
    admissionLocationFunctions : PropTypes.object.isRequired
};