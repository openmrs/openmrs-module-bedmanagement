import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';

import UrlHelper from 'utilities/urlHelper';

export default class AddEditBed extends React.Component {
    constructor(props, context) {
        super(props, context);

        this.state = {
            bedUuid: props.bed.bedUuid != null ? props.bed.bedUuid : null,
            row: props.bed.rowNumber != null ? props.bed.rowNumber : 1,
            column: props.bed.columnNumber != null ? props.bed.columnNumber : 1,
            bedNumber: props.bed.bedNumber != null ? props.bed.bedNumber : '',
            bedTypeName: props.bed.bedType != null ? props.bed.bedType.name : props.bedTypes[0].name,
            disableSubmit: false,
            rowFieldError: '',
            columnFieldError: ''
        };

        this.intl = context.intl;
        this.urlHelper = new UrlHelper();
        this.admissionLocation = props.admissionLocationFunctions.getAdmissionLocationByUuid(props.activeUuid);
        this.onChangeRowField = this.onChangeRowField.bind(this);
        this.onChangeColumnField = this.onChangeColumnField.bind(this);
        this.onChangeBedNumberField = this.onChangeBedNumberField.bind(this);
        this.onChangeBedType = this.onChangeBedType.bind(this);
        this.onSubmitHandler = this.onSubmitHandler.bind(this);
        this.cancelEventHandler = this.cancelEventHandler.bind(this);
    }

    componentWillUpdate(nextProps, nextState) {
        if (this.props.activeUuid != nextProps.activeUuid) {
            this.admissionLocation = nextProps.admissionLocationFunctions.getAdmissionLocationByUuid(
                nextProps.activeUuid
            );
        }
    }

    onChangeRowField() {
        if (this.rowField.value >= 1 && this.rowField.value <= this.props.layoutRow) {
            this.setState({
                rowFieldError: '',
                row: Number(this.rowField.value)
            });
        } else {
            let errorMsg = '';
            if (this.rowField.value == '') {
                errorMsg = this.intl.formatMessage({id: 'ROW_REQUIRED_MSG'});
            } else if (this.rowField.value <= 0) {
                errorMsg = this.intl.formatMessage({id: 'ROW_SHOULD_GREATER_THAN_ZERO'});
            } else if (this.rowField.value > this.props.layoutRow) {
                errorMsg = this.intl.formatMessage({id: 'ROW_SHOULD_GREATER_THAN_LAYOUT_ROW'});
            }

            this.setState({
                rowFieldError: errorMsg,
                row: this.rowField.value
            });
        }
    }

    onChangeColumnField() {
        if (this.columnField.value >= 1 && this.columnField.value <= this.props.layoutColumn) {
            this.setState({
                columnFieldError: '',
                column: Number(this.columnField.value)
            });
        } else {
            let errorMsg = '';
            if (this.columnField.value == '') {
                errorMsg = this.intl.formatMessage({id: 'COLUMN_REQUIRED_MSG'});
            } else if (this.columnField.value <= 0) {
                errorMsg = this.intl.formatMessage({id: 'COLUMN_SHOULD_GREATER_THAN_ZERO'});
            } else if (this.columnField.value > this.props.layoutColumn) {
                errorMsg = this.intl.formatMessage({id: 'COLUMN_SHOULD_GREATER_THAN_LAYOUT_COLUMN'});
            }

            this.setState({
                columnFieldError: errorMsg,
                column: this.columnField.value
            });
        }
    }

    onChangeBedNumberField() {
        this.setState({
            bedNumber: this.bedNumberField.value
        });
    }

    onChangeBedType() {
        this.setState({
            bedTypeName: this.bedTypeSelector.value
        });
    }

    onSubmitHandler(event) {
        event.preventDefault();
        if (this.state.rowFieldError != '' || this.state.columnFieldError != '') {
            const errorMsg = this.intl.formatMessage({id: 'FIX_ERROR_MSG'});
            this.props.admissionLocationFunctions.notify(errorText, errorMsg);
            return;
        }

        this.setState({
            disableSubmit: true
        });
        const self = this;
        const parameters = {
            bedNumber: this.state.bedNumber,
            bedType: this.state.bedTypeName,
            row: this.state.row,
            column: this.state.column,
            locationUuid: this.props.activeUuid
        };

        axios({
            method: 'post',
            url: this.urlHelper.apiBaseUrl() + (this.state.bedUuid != null ? '/bed/' + this.state.bedUuid : '/bed'),
            headers: {'Content-Type': 'application/json'},
            data: parameters
        })
            .then(function(response) {
                self.setState({
                    bedUuid: response.data.uuid,
                    disableSubmit: false
                });

                const sussesMsg = self.intl.formatMessage({id: 'BED_SAVE_MSG'});
                self.props.admissionLocationFunctions.notify('success', sussesMsg);
                self.props.admissionLocationFunctions.setState({
                    activePage: 'listing',
                    pageData: {},
                    activeUuid: self.props.activeUuid
                });
            })
            .catch(function(errorResponse) {
                self.setState({
                    disableSubmit: false
                });

                const error = errorResponse.response.data ? errorResponse.response.data.error : errorResponse;
                self.props.admissionLocationFunctions.notify('error', error.message.replace(/\[|\]/g, ''));
            });
    }

    cancelEventHandler(event) {
        event.preventDefault();
        this.props.admissionLocationFunctions.setState({
            activePage: 'listing',
            pageData: {},
            activeUuid: this.props.activeUuid
        });
    }

    render() {
        return (
            <div className="main-container">
                <fieldset className="admission-location-form">
                    <legend>
                        &nbsp;{' '}
                        {this.props.operation == 'add'
                            ? this.intl.formatMessage({id: 'ADD'})
                            : this.intl.formatMessage({id: 'EDIT'})}{' '}
                        {this.intl.formatMessage({id: 'BED'})} &nbsp;
                    </legend>
                    <div className="block-content">
                        <form onSubmit={this.onSubmitHandler}>
                            <div className="form-block">
                                <label className="form-title inline">
                                    {this.intl.formatMessage({id: 'LOCATION'})}:
                                </label>
                                <span id="location-name">{this.admissionLocation.name}</span>
                            </div>
                            <div className="form-block">
                                <label className="form-title">{this.intl.formatMessage({id: 'ROW'})}:</label>
                                <input
                                    type="number"
                                    value={this.state.row}
                                    ref={(input) => {
                                        this.rowField = input;
                                    }}
                                    required={true}
                                    onChange={this.onChangeRowField}
                                    id="row-field"
                                />
                                {this.state.rowFieldError != '' ? (
                                    <p className="error">{this.state.rowFieldError}</p>
                                ) : (
                                    ''
                                )}
                            </div>
                            <div className="form-block">
                                <label className="form-title">{this.intl.formatMessage({id: 'COLUMN'})}:</label>
                                <input
                                    type="number"
                                    value={this.state.column}
                                    ref={(input) => {
                                        this.columnField = input;
                                    }}
                                    required={true}
                                    onChange={this.onChangeColumnField}
                                    id="column-field"
                                />
                                {this.state.columnFieldError != '' ? (
                                    <p className="error">{this.state.columnFieldError}</p>
                                ) : (
                                    ''
                                )}
                            </div>
                            <div className="form-block">
                                <label className="form-title">{this.intl.formatMessage({id: 'BED_NUMBER'})}:</label>
                                <input
                                    type="text"
                                    value={this.state.bedNumber}
                                    ref={(input) => {
                                        this.bedNumberField = input;
                                    }}
                                    required={true}
                                    onChange={this.onChangeBedNumberField}
                                    id="bed-number-field"
                                    maxLength={10}
                                />
                            </div>
                            <div className="form-block">
                                <label className="form-title">{this.intl.formatMessage({id: 'BED_TYPE'})}:</label>
                                <select
                                    onChange={this.onChangeBedType}
                                    required={true}
                                    id="bed-type"
                                    ref={(dropDown) => (this.bedTypeSelector = dropDown)}
                                    value={this.state.bedTypeName != null ? this.state.bedTypeName : ''}>
                                    {this.props.bedTypes.map((bedType) => (
                                        <option key={bedType.uuid} value={bedType.name}>
                                            {bedType.name}
                                        </option>
                                    ))}
                                </select>
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

AddEditBed.propTypes = {
    activeUuid: PropTypes.string,
    bed: PropTypes.shape({
        bedNumber: PropTypes.string,
        bedUuid: PropTypes.string,
        bedType: PropTypes.object,
        columnNumber: PropTypes.number,
        rowNumber: PropTypes.number,
        status: PropTypes.string
    }).isRequired,
    layoutRow: PropTypes.number.isRequired,
    layoutColumn: PropTypes.number.isRequired,
    bedTypes: PropTypes.array.isRequired,
    operation: PropTypes.string.isRequired,
    admissionLocationFunctions: PropTypes.object.isRequired
};

AddEditBed.contextTypes = {
    intl: PropTypes.object
};
