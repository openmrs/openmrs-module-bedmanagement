import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';

import UrlHelper from 'utilities/urlHelper';

require('./admissionLocationForm.css');
export default class AddEditAdmissionLocation extends React.Component {
    constructor(props) {
        super(props);

        this.urlHelper = new UrlHelper();
        this.initData = this.initData.bind(this);
        this.initData();

        this.state = {
            uuid: this.admissionLocation != null ? this.admissionLocation.uuid : null,
            name: this.admissionLocation != null ? this.admissionLocation.name : '',
            description: this.admissionLocation != null && this.admissionLocation.description != null ? this.admissionLocation.description : '',
            parentAdmissionLocationUuid: this.parentAdmissionLocationUuid,
            disableSubmit: false
        };

        this.onChangeNameField = this.onChangeNameField.bind(this);
        this.onChangeDescriptionField = this.onChangeDescriptionField.bind(this);
        this.onSelectParentLocation = this.onSelectParentLocation.bind(this);
        this.cancelEventHandler = this.cancelEventHandler.bind(this);
        this.onSubmitHandler = this.onSubmitHandler.bind(this);
    }

    initData() {
        this.admissionLocation = this.props.operation == 'add' ? null :
            this.props.admissionLocationFunctions.getAdmissionLocationByUuid(this.props.activeUuid);
        this.parentAdmissionLocationUuid = this.props.operation == 'add' ? this.props.activeUuid
            : (this.admissionLocation != null ? this.admissionLocation.parentAdmissionLocationUuid : null);
        this.visitLocations = this.props.admissionLocationFunctions.getVisitLocations();
        this.parentAdmissionLocation = this.parentAdmissionLocationUuid != null ?
            this.props.admissionLocationFunctions.getAdmissionLocationByUuid(this.parentAdmissionLocationUuid) : null;
    }

    componentWillUpdate(nextProps, nextState) {
        this.initData();
    }

    onChangeNameField() {
        this.setState({
            name: this.nameField.value
        });
    }

    onChangeDescriptionField() {
        this.setState({
            description: this.descriptionField.value
        });
    }

    onSelectParentLocation() {
        this.setState({
            parentAdmissionLocationUuid: this.parentSelector.value != '' ? this.parentSelector.value : null
        });
    }

    cancelEventHandler(event) {
        event.preventDefault();
        this.props.admissionLocationFunctions.setState({
            activePage: 'listing',
            pageData: {},
            activeUuid: this.parentAdmissionLocation != null ? this.parentAdmissionLocation.uuid : null
        });
    }

    onSubmitHandler(event) {
        event.preventDefault();
        this.setState({
            disableSubmit: true
        });
        const self = this;
        const parameters = {
            parentLocationUuid: this.state.parentAdmissionLocationUuid,
            name: this.state.name,
            description: this.state.description
        };

        axios({
            method: 'post',
            url: this.urlHelper.apiBaseUrl() + (this.state.uuid != null ? '/admissionLocation/' + this.state.uuid : '/admissionLocation'),
            headers: {'Content-Type': 'application/json'},
            data: parameters,
        }).then(function (response) {
            self.setState({
                disableSubmit: false
            });
            self.props.admissionLocationFunctions.setState({
                activeUuid: response.data.ward.uuid
            });

            self.props.admissionLocationFunctions.notify('success', 'Admission location save successfully');
            self.props.admissionLocationFunctions.reFetchAllAdmissionLocations();
            self.props.admissionLocationFunctions.setState({
                activePage: 'listing',
                pageData: {},
                activeUuid: self.parentAdmissionLocation != null ? self.parentAdmissionLocation.uuid : null
            });
        }).catch(function (error) {
            this.setState({
                disableSubmit: false
            });
            self.props.admissionLocationFunctions.notify('error', error.message);
        });
    }

    render() {
        return <div className="main-container">
            <fieldset className="admission-location-form">
                <legend>&nbsp; {this.props.operation == 'add' ? 'Add' : 'Edit'} Ward &nbsp;</legend>
                <div className="block-content">
                    <form onSubmit={this.onSubmitHandler}>
                        <div className="form-block">
                            {this.parentAdmissionLocation != null ? <label className="form-title inline">Parent Location:</label>:
                                <label className="form-title">Parent Location:</label>}
                            {this.parentAdmissionLocation != null ? <span>{this.parentAdmissionLocation.name}</span> :
                                <select name="parent-location" onChange={this.onSelectParentLocation}
                                    ref={(dropDown) => this.parentSelector = dropDown}
                                    value={this.state.parentAdmissionLocationUuid != null ? this.state.parentAdmissionLocationUuid : ''}>
                                    <option value="">None</option>
                                    {Object.keys(this.visitLocations).map((key) =>
                                        <option key={key} value={this.visitLocations[key].uuid}>
                                            {this.visitLocations[key].name}
                                        </option>)}
                                </select>}
                        </div>
                        <div className="form-block">
                            <label className="form-title">Name:</label>
                            <input type="text" onChange={this.onChangeNameField} value={this.state.name} required={true}
                                ref={(input) => {this.nameField = input;}}/>
                        </div>
                        <div className="form-block">
                            <label className="form-title">Description:</label>
                            <textarea name="description" rows="4" cols="50" onChange={this.onChangeDescriptionField}
                                value={this.state.description} ref={(textArea) => this.descriptionField = textArea}></textarea>
                        </div>
                        <div className="form-block">
                            <input type="submit" name="submit" value={this.state.disableSubmit ? 'Saving...' : 'Save'}
                                disabled={this.state.disableSubmit} className="form-btn float-left margin-right"/>
                            <input type="button" onClick={this.cancelEventHandler} name="cancel" value="Cancel"
                                className="form-btn float-left"/>
                        </div>
                    </form>
                </div>
            </fieldset>
        </div>;
    }
}

AddEditAdmissionLocation.defaultProps = {
    activeUuid: null,
    operation: 'add'
};

AddEditAdmissionLocation.propTypes = {
    activeUuid: PropTypes.string,
    operation: PropTypes.string,
    admissionLocationFunctions: PropTypes.object.isRequired
};