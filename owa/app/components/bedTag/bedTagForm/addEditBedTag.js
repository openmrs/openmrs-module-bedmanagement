import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';

import UrlHelper from 'utilities/urlHelper';

require('./bedTagForm.css');
export default class AddEditBedTag extends React.Component {
    constructor(props, context) {
        super(props, context);

        this.intl = context.intl;
        this.bedTag = props.bedTagFunctions.getBedTagByUuid(props.bedTagUuid);
        this.state = {
            uuid: this.bedTag != null ? this.bedTag.uuid : null,
            name: this.bedTag != null ? this.bedTag.name : '',
            disableSubmit: false
        };

        this.urlHelper = new UrlHelper();
        this.onChangeNameField = this.onChangeNameField.bind(this);
        this.cancelEventHandler = this.cancelEventHandler.bind(this);
        this.onSubmitHandler = this.onSubmitHandler.bind(this);
    }

    onChangeNameField() {
        this.setState({
            name: this.nameField.value
        });
    }

    cancelEventHandler(event) {
        event.preventDefault();

        this.props.bedTagFunctions.setState({
            activePage: 'listing',
            pageData: {}
        });
    }

    onSubmitHandler(event) {
        event.preventDefault();

        const existingTags = this.props.bedTagFunctions.getAllBedTags() || [];
        const duplicate = existingTags.some(
            (tag) =>
                tag.name.toLowerCase() === this.state.name.trim().toLowerCase() &&
                tag.uuid !== this.state.uuid
        );

        if (duplicate) {
            this.props.bedTagFunctions.notify(
                'error',
                this.intl.formatMessage({id: 'BED_TAG_DUPLICATE_MSG'}, {name: this.state.name})
            );
            return;
        }

        this.setState({ disableSubmit: true });
        const self = this;
        const parameters = {
            name: this.state.name
        };

        axios({
            method: 'post',
            url: this.urlHelper.apiBaseUrl() + (this.state.uuid != null ? '/bedTag/' + this.state.uuid : '/bedTag'),
            headers: {'Content-Type': 'application/json'},
            data: parameters
        })
            .then(function(response) {
                self.setState({
                    uuid: response.data.uuid,
                    disableSubmit: false
                });

                self.props.bedTagFunctions.fetchBedTags();
                const successMsg = self.intl.formatMessage({id: 'BED_TAG_SAVE_MSG'});
                self.props.bedTagFunctions.notify('success', successMsg);
                self.props.bedTagFunctions.setState({
                    activePage: 'listing',
                    pageData: {}
                });
            })
            .catch(function(errorResponse) {
                self.setState({
                    disableSubmit: false
                });
                const error = errorResponse.response?.data?.error || errorResponse;
                self.props.bedTagFunctions.notify('error', error.message.replace(/\[|\]/g, ''));
            });
    }

    render() {
        return (
            <div className="form-container">
                <fieldset className="bed-tag-form">
                    <legend>
                        &nbsp;{' '}
                        {this.props.operation == 'add'
                            ? this.intl.formatMessage({id: 'ADD'})
                            : this.intl.formatMessage({id: 'EDIT'})}{' '}
                        {this.intl.formatMessage({id: 'BED_TAG'})} &nbsp;
                    </legend>
                    <div className="block-content">
                        <form onSubmit={this.onSubmitHandler}>
                            <div className="form-block">
                                <label className="form-title">{this.intl.formatMessage({id: 'NAME'})}:</label>
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
                                <input
                                    type="submit"
                                    name="submit"
                                    value={
                                        this.state.disableSubmit
                                            ? this.intl.formatMessage({id: 'SAVING'})
                                            : this.intl.formatMessage({id: 'SAVE'})
                                    }
                                    disabled={this.state.disableSubmit}
                                    className="form-btn float-left margin-right"
                                />
                                <input
                                    type="button"
                                    onClick={this.cancelEventHandler}
                                    name="cancel"
                                    value={this.intl.formatMessage({id: 'CANCEL'})}
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

AddEditBedTag.propTypes = {
    bedTagUuid: PropTypes.string,
    bedTagFunctions: PropTypes.object.isRequired
};

AddEditBedTag.contextTypes = {
    intl: PropTypes.object
};
