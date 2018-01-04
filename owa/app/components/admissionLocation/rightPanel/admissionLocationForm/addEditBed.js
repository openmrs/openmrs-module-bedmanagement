import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';

import UrlHelper from 'utilities/urlHelper';

export default class AddEditBed extends React.Component {
    constructor(props) {
        super(props);

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
            this.admissionLocation = nextProps.admissionLocationFunctions.getAdmissionLocationByUuid(nextProps.activeUuid);
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
                errorMsg = 'Row is required field';
            } else if (this.rowField.value <= 0) {
                errorMsg = 'Row value should be greater than 0';
            } else if (this.rowField.value > this.props.layoutRow) {
                errorMsg = 'Row value should not be greater than layout row size';
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
                errorMsg = 'Column is required field';
            } else if (this.columnField.value <= 0) {
                errorMsg = 'Column value should be greater than 0';
            } else if (this.columnField.value > this.props.layoutColumn) {
                errorMsg = 'Column value should not be greater than layout column size';
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
        if(this.state.rowFieldError != '' || this.state.columnFieldError != ''){
            this.props.admissionLocationFunctions.notify('error', 'Fix error before submit');
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
            url: this.urlHelper.apiBaseUrl() + (this.state.bedUuid != null ? ('/bed/' + this.state.bedUuid) : '/bed'),
            headers: {'Content-Type': 'application/json'},
            data: parameters,
        }).then(function (response) {
            self.setState({
                bedUuid: response.data.uuid,
                disableSubmit: false
            });

            self.props.admissionLocationFunctions.notify('success', 'Bed save successfully');
            self.props.admissionLocationFunctions.setState({
                activePage: 'listing',
                pageData: {},
                activeUuid: self.props.activeUuid
            });
        }).catch(function (error) {
            self.setState({
                disableSubmit: false
            });
            self.props.admissionLocationFunctions.notify('error', error.message);
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
        return <div className="main-container">
            <div className="admission-location-form">
                <div className="block">
                    <div className="block-title">
                        {this.props.operation == 'add' ? 'Add' : 'Edit'} Bed
                        <a href="javascript:void(0);" className="back-link" title="back" onClick={this.cancelEventHandler}>
                            &lt;&lt; Back
                        </a>
                    </div>
                    <div className="block-content">
                        <form onSubmit={this.onSubmitHandler}>
                            <div className="form-block">
                                <label className="form-title">Location</label>
                                <span id="location-name">{this.admissionLocation.name}</span>
                            </div>
                            <div className="form-block">
                                <label className="form-title">Row</label>
                                <input type="number" value={this.state.row} ref={(input) => {this.rowField = input;}}
                                    required={true} onChange={this.onChangeRowField} id="row-field"/>
                                {this.state.rowFieldError != '' ? <p className="error">{this.state.rowFieldError}</p> : ''}
                            </div>
                            <div className="form-block">
                                <label className="form-title">Column</label>
                                <input type="number" value={this.state.column} ref={(input) => {this.columnField = input;}}
                                    required={true} onChange={this.onChangeColumnField} id="column-field"/>
                                {this.state.columnFieldError != '' ? <p className="error">{this.state.columnFieldError}</p> : ''}
                            </div>
                            <div className="form-block">
                                <label className="form-title">Bed Number</label>
                                <input type="text" value={this.state.bedNumber} ref={(input) => {this.bedNumberField = input;}}
                                    required={true} onChange={this.onChangeBedNumberField} id="bed-number-field"/>
                            </div>
                            <div className="form-block">
                                <label className="form-title">Bed Type</label>
                                <select onChange={this.onChangeBedType} required={true} id="bed-type"
                                    ref={(dropDown) => this.bedTypeSelector = dropDown}
                                    value={this.state.bedTypeName != null ? this.state.bedTypeName : ''}>
                                    {this.props.bedTypes.map((bedType) =>
                                        <option key={bedType.id} value={bedType.name}>
                                            {bedType.name}
                                        </option>)}
                                </select>
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