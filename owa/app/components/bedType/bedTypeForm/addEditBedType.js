import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';

import UrlHelper from 'utilities/urlHelper';

require('./bedTypeForm.css');
export default class AddEditBedType extends React.Component {
    constructor(props) {
        super(props);

        this.bedType = props.bedTypeFunctions.getBedTypeByUuid(props.bedTypeUuid);
        this.state = {
            uuid: this.bedType != null ? this.bedType.uuid : null,
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
            url: this.urlHelper.apiBaseUrl() + (this.state.uuid != null ? '/bedtype/' + this.state.uuid : '/bedtype'),
            headers: {'Content-Type': 'application/json'},
            data: parameters
        })
            .then(function(response) {
                self.setState({
                    uuid: response.data.uuid,
                    disableSubmit: false
                });

                self.props.bedTypeFunctions.fetchBedTypes();
                self.props.bedTypeFunctions.notify('success', 'Bed Type save successfully');
                self.props.bedTypeFunctions.setState({
                    activePage: 'listing',
                    pageData: {}
                });
            })
            .catch(function(errorResponse) {
                self.setState({
                    disableSubmit: false
                });

                const error = errorResponse.response.data ? errorResponse.response.data.error : errorResponse;
                self.props.bedTypeFunctions.notify('error', error.message.replace(/\[|\]/g, ''));
            });
    }

    render() {
        return (
            <div className="form-container">
                <fieldset className="bed-type-form">
                    <legend>&nbsp; {this.props.operation == 'add' ? 'Add' : 'Edit'} Bed Type &nbsp;</legend>
                    <div className="block-content">
                        <form onSubmit={this.onSubmitHandler}>
                            <div className="form-block">
                                <label className="form-title">Name:</label>
                                <input
                                    type="text"
                                    value={this.state.name}
                                    ref={(input) => {
                                        this.nameField = input;
                                    }}
                                    required={true}
                                    onChange={this.onChangeNameField}
                                    id="name-field"
                                />
                            </div>
                            <div className="form-block">
                                <label className="form-title">Display Name:</label>
                                <input
                                    type="text"
                                    value={this.state.displayName}
                                    ref={(input) => {
                                        this.displayNameField = input;
                                    }}
                                    required={true}
                                    onChange={this.onChangeDisplayNameField}
                                    id="display-name-field"
                                />
                            </div>
                            <div className="form-block">
                                <label className="form-title">Description:</label>
                                <textarea
                                    value={this.state.description}
                                    ref={(input) => {
                                        this.descriptionField = input;
                                    }}
                                    onChange={this.onChangeDescription}
                                    id="description-field"
                                />
                            </div>
                            <div className="form-block">
                                <input
                                    type="submit"
                                    name="submit"
                                    value={this.state.disableSubmit ? 'Saving...' : 'Save'}
                                    disabled={this.state.disableSubmit}
                                    className="form-btn float-left margin-right"
                                />
                                <input
                                    type="button"
                                    onClick={this.cancelEventHandler}
                                    name="cancel"
                                    value="Cancel"
                                    className="form-btn float-left"
                                />
                            </div>
                        </form>
                    </div>
                </fieldset>
            </div>
        );
    }
}

AddEditBedType.propTypes = {
    bedTypeUuid: PropTypes.string,
    bedTypeFunctions: PropTypes.object.isRequired
};
