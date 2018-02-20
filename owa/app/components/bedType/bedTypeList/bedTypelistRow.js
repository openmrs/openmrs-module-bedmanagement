import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';

import UrlHelper from 'utilities/urlHelper';

export default class BedTypeListRow extends React.Component {
    constructor(props) {
        super(props);

        this.urlHelper = new UrlHelper();
        this.deleteHandler = this.deleteHandler.bind(this);
        this.editHandler = this.editHandler.bind(this);
    }

    deleteHandler(event) {
        event.preventDefault();

        const self = this;
        const confirmation = confirm('Are you sure you want to delete bed type ' + this.props.bedType.name + '?');
        if (confirmation) {
            axios({
                method: 'delete',
                url: this.urlHelper.apiBaseUrl() + '/bedtype/' + this.props.bedType.uuid
            })
                .then(function() {
                    self.props.bedTypeFunctions.notify('success', 'Delete successfully');
                    self.props.bedTypeFunctions.fetchBedTypes();
                })
                .catch(function(errorResponse) {
                    const error = errorResponse.response.data ? errorResponse.response.data.error : errorResponse;
                    self.props.bedTypeFunctions.notify('error', error.message.replace(/\[|\]/g, ''));
                });
        }
    }

    editHandler(event) {
        event.preventDefault();

        this.props.bedTypeFunctions.setState({
            activePage: 'addEdit',
            pageData: {
                operation: 'edit',
                bedTypeUuid: this.props.bedType.uuid
            }
        });
    }

    render() {
        return (
            <tr>
                <td>{this.props.bedType.name}</td>
                <td>{this.props.bedType.displayName}</td>
                <td>{this.props.bedType.description}</td>
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

BedTypeListRow.propTypes = {
    bedType: PropTypes.object.isRequired,
    bedTypeFunctions: PropTypes.object.isRequired
};
