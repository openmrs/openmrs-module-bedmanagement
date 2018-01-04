import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';

import UrlHelper from 'utilities/urlHelper';

require('./bedTypeForm.css');
export default class AddEditBedType extends React.Component {
    constructor(props) {
        super(props);

        this.bedType = props.bedTypeFunctions.getBedTypeById(props.bedTypeId);
        this.state = {
            id: this.bedType != null ? this.bedType.id : null,
            name: this.bedType != null ? this.bedType.name : '',
            displayName: this.bedType != null ? this.bedType.displayName : '',
            description: this.bedType != null && this.bedType.description != null ? this.bedType.description : '',
            disableSubmit: false
        };

        this.urlHelper = new UrlHelper();
        this.onChangeNameField = this.onChangeNameField.bind(this);
        this.onChangeDisplayNameField = this.onChangeDisplayNameField.bind(this);
        this.onChangeDescription = this.onChangeDescription.bind(this);
        this.cancelEventHandler = this.cancelEventHandler.bind(this);
        this.onSubmitHandler = this.onSubmitHandler.bind(this);
    }

    onChangeNameField() {
        this.setState({
            name: this.nameField.value
        });
    }

    onChangeDisplayNameField() {
        this.setState({
            displayName: this.displayNameField.value
        });
    }

    onChangeDescription() {
        this.setState({
            description: this.descriptionField.value
        });
    }

    cancelEventHandler(event) {
        event.preventDefault();

        this.props.bedTypeFunctions.setState({
            activePage: 'listing',
            pageData: {}
        });
    }

    onSubmitHandler(event) {
        event.preventDefault();

        this.setState({
            disableSubmit: true
        });
        const self = this;
        const parameters = {
            name: this.state.name,
            displayName: this.state.displayName,
            description: this.state.description
        };

        axios({
            method: 'post',
            url: this.urlHelper.apiBaseUrl() + (this.state.id != null ? ('/bedtype/' + this.state.id) : '/bedtype'),
            headers: {'Content-Type': 'application/json'},
            data: parameters,
        }).then(function (response) {
            self.setState({
                id: response.data.id,
                disableSubmit: false
            });

            self.props.bedTypeFunctions.fetchBedTypes();
            self.props.bedTypeFunctions.notify('success', 'Bed Type save successfully');
            self.props.bedTypeFunctions.setState({
                activePage: 'listing',
                pageData: {}
            });
        }).catch(function (error) {
            self.setState({
                disableSubmit: false
            });
            self.props.bedTypeFunctions.notify('error', error.message);
        });
    }

    render() {
        return <div className="form-container">
            <div className="bed-type-form">
                <div className="block">
                    <div className="block-title">
                        {this.props.operation == 'add' ? 'Add' : 'Edit'} Bed Type
                        <a href="javascript:void(0);" className="back-link" title="back" onClick={this.cancelEventHandler}>
                            &lt;&lt; Back
                        </a>
                    </div>
                    <div className="block-content">
                        <form onSubmit={this.onSubmitHandler}>
                            <div className="form-block">
                                <label className="form-title">Name</label>
                                <input type="text" value={this.state.name} ref={(input) => {this.nameField = input;}}
                                    required={true} onChange={this.onChangeNameField} id="name-field"/>
                            </div>
                            <div className="form-block">
                                <label className="form-title">Display Name</label>
                                <input type="text" value={this.state.displayName} ref={(input) => {this.displayNameField = input;}}
                                    required={true} onChange={this.onChangeDisplayNameField} id="display-name-field"/>
                            </div>
                            <div className="form-block">
                                <label className="form-title">Description</label>
                                <textarea value={this.state.description} ref={(input) => {this.descriptionField = input;}}
                                    onChange={this.onChangeDescription} id="description-field"></textarea>
                            </div>
                            <div className="form-block">
                                <input type="submit" name="submit" value={this.state.disableSubmit ? 'Saving...' : 'Save'}
                                    disabled={this.state.disableSubmit} className="btn btn-primary float-left margin-right"/>
                                <input type="button" onClick={this.cancelEventHandler}
                                    name="submit" value="Cancel" className="btn btn-danger float-left"/>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>;
    }
}

AddEditBedType.propTypes = {
    bedTypeId: PropTypes.number,
    bedTypeFunctions: PropTypes.object.isRequired
};