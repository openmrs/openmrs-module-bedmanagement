import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';

import UrlHelper from 'utilities/urlHelper';

require('./bedTagForm.css');
export default class AddEditBedTag extends React.Component {
    constructor(props) {
        super(props);

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

        this.setState({
            disableSubmit: true
        });
        const self = this;
        const parameters = {
            name: this.state.name
        };

        axios({
            method: 'post',
            url: this.urlHelper.apiBaseUrl() + (this.state.uuid != null ? ('/bedTag/' + this.state.uuid) : '/bedTag'),
            headers: {'Content-Type': 'application/json'},
            data: parameters,
        }).then(function (response) {
            self.setState({
                uuid: response.data.uuid,
                disableSubmit: false
            });

            self.props.bedTagFunctions.fetchBedTags();
            self.props.bedTagFunctions.notify('success', 'Bed Tag save successfully');
            self.props.bedTagFunctions.setState({
                activePage: 'listing',
                pageData: {}
            });
        }).catch(function (error) {
            self.setState({
                disableSubmit: false
            });
            self.props.bedTagFunctions.notify('error', error.message);
        });
    }

    render() {
        return <div className="form-container">
            <div className="bed-tag-form">
                <div className="block">
                    <div className="block-title">
                        {this.props.operation == 'add' ? 'Add' : 'Edit'} Bed Tag
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

AddEditBedTag.propTypes = {
    bedTagUuid: PropTypes.string,
    bedTagFunctions: PropTypes.object.isRequired
};