import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';

import UrlHelper from 'utilities/urlHelper';

export default class BedTagListRow extends React.Component {
    constructor(props) {
        super(props);

        this.urlHelper = new UrlHelper();
        this.deleteHandler = this.deleteHandler.bind(this);
        this.editHandler = this.editHandler.bind(this);
    }

    deleteHandler(event) {
        event.preventDefault();

        const self = this;
        const confirmation = confirm('Are you sure you want to delete bed Tag ' + this.props.bedTag.name + '?');
        if (confirmation) {
            axios({
                method: 'delete',
                url: this.urlHelper.apiBaseUrl() + '/bedTag/' + this.props.bedTag.uuid
            })
                .then(function() {
                    self.props.bedTagFunctions.notify('success', 'Delete successfully');
                    self.props.bedTagFunctions.fetchBedTags();
                })
                .catch(function(errorResponse) {
                    const error = errorResponse.response.data ? errorResponse.response.data.error : errorResponse;
                    self.props.bedTagFunctions.notify('error', error.message.replace(/\[|\]/g, ''));
                });
        }
    }

    editHandler(event) {
        event.preventDefault();

        this.props.bedTagFunctions.setState({
            activePage: 'addEdit',
            pageData: {
                operation: 'edit',
                bedTagUuid: this.props.bedTag.uuid
            }
        });
    }

    render() {
        return (
            <tr>
                <td>{this.props.bedTag.name}</td>
                <td />
                <td>
                    <a href="javascript:void(0);" onClick={this.editHandler}>
                        <i className="icon fa fa-edit" aria-hidden="true" /> Edit
                    </a>
                    &nbsp; | &nbsp;
                    <a href="javascript:void(0);" onClick={this.deleteHandler}>
                        <i className="icon fa fa-trash" aria-hidden="true" /> Delete
                    </a>
                </td>
            </tr>
        );
    }
}

BedTagListRow.propTypes = {
    bedTag: PropTypes.object.isRequired,
    bedTagFunctions: PropTypes.object.isRequired
};
