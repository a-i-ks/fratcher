/**
 * CSS template from https://www.bootply.com/sY7gQy6XF7
 */

import React from "react";

import User from "../util/User";

import axios from "axios";
import UserAvatar from "react-user-avatar";
import {Button, Col, ControlLabel, Form, FormControl, FormGroup} from "react-bootstrap";

class EditProfile extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            user: undefined,
            name: "",
            nameValidationError: null,
            email: "",
            emailValidationError: null,
            username: "",
            usernameValidationError: null,
            aboutMe: "",
            aboutMeValidationError: null,
            error: undefined,
            errorText: ""
        };
        this.handleNameChange = this.handleNameChange.bind(this);
        this.handleNameChangeEnd = this.handleNameChangeEnd.bind(this);
        this.handleEmailChange = this.handleEmailChange.bind(this);
        this.handleEmailChangeEnd = this.handleEmailChangeEnd.bind(this);
        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handleUsernameChangeEnd = this.handleUsernameChangeEnd.bind(this);
        this.handleAboutMeChange = this.handleAboutMeChange.bind(this);
        this.handleAboutMeChangeEnd = this.handleAboutMeChangeEnd.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
        this.handlePasswordConfirmChange = this.handlePasswordConfirmChange.bind(this);

        this.validateName = this.validateName.bind(this)

        this.handleCancel = this.handleCancel.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.postUserObj = this.postUserObj.bind(this);
    }

    // This function is called before render() to initialize its state.
    componentWillMount() {
        axios.get('/api/user/' + User.id)
            .then(({data}) => {
                this.setState({
                    origUser: data,
                    user: data,
                    name: (data.profile.name === null || undefined) ? "" : data.profile.name,
                    email: (data.email === null || undefined) ? "" : data.email,
                    username: (data.username === null || undefined) ? "" : data.username,
                    aboutMe: (data.profile.aboutMe === null || undefined) ? "" : data.profile.aboutMe
                })
            });
    }

    handleNameChange(event) {
        this.setState({name: event.target.value});
    }

    handleNameChangeEnd(event) {
        this.setState({name: event.target.value.trim()}, this.validateName(this.state.name));
    }

    validateName(value) {
        // regex from https://stackoverflow.com/a/2044909/3898604
        var re = /^([ \u00c0-\u01ffa-zA-Z'\-])+$/g;
        if (!re.test(value)) {
            this.setState({nameValidationError: "error"});
        } else {
            this.setState({nameValidationError: null});
        }
    }

    handleEmailChange(event) {
        this.setState({email: event.target.value.trim()});
    }

    handleEmailChangeEnd(event) {
        this.setState({email: event.target.value.trim()}, this.validateEmail(this.state.email));
    }

    validateEmail(value) {
        // regex from http://stackoverflow.com/questions/46155/validate-email-address-in-javascript
        var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        if (!re.test(value)) {
            this.setState({emailValidationError: "error"});
        } else {
            this.setState({emailValidationError: null});
        }
    }

    handleUsernameChange(event) {
        this.setState({username: event.target.value.trim()});
    }

    handleUsernameChangeEnd(event) {
        this.setState({username: event.target.value.trim()}, this.validateUsername(this.state.username));
    }

    validateUsername(value) {
        // regex from https://stackoverflow.com/a/2044909/3898604
        var re = /^[a-zA-z_0-9]{3,20}$/;
        if (!re.test(value)) {
            this.setState({usernameValidationError: "error"});
        } else {
            this.setState({usernameValidationError: null});
        }
    }

    handleAboutMeChange(event) {
        this.setState({aboutMe: event.target.value.trim()});
    }

    handleAboutMeChangeEnd(event) {
        this.setState({aboutMe: event.target.value.trim()});
    }

    validateAboutMe(value) {

    }

    handlePasswordChange(event) {
        this.setState({password: event.target.value.trim()});
    }

    handlePasswordConfirmChange(event) {
        this.setState({passwordConfirmation: event.target.value.trim()});
    }


    handleSubmit() {
        this.render();
        this.setState({
            user: Object.assign({}, this.state.user, {
                profile: Object.assign({}, this.state.user.profile, {
                    name: this.state.name,
                    aboutMe: this.state.aboutMe
                }),
                username: this.state.username,
                email: this.state.email
            }),
        }, this.postUserObj(this.state.user));
    }

    handleCancel() {
        this.setState({
            name: (this.state.origUser.profile.name === null || undefined) ? "" : this.state.origUser.profile.name,
            email: (this.state.origUser.email === null || undefined) ? "" : this.state.origUser.email,
            username: (this.state.origUser.username === null || undefined) ? "" : this.state.origUser.username,
            aboutMe: (this.state.origUser.profile.aboutMe === null || undefined) ? "" : this.state.origUser.profile.aboutMe,
            nameValidationError: null,
            emailValidationError: null,
            usernameValidationError: null,
            aboutMeValidationError: null
        });
    }

    postUserObj(user) {
        event.preventDefault();
        let comments = [];
        if (this.state.comment) {
            comments = [{text: this.state.comment}];
        }
        axios.post('/api/user',
            {
                title: this.state.title,
                comments: comments
            })
            .then((data) => {
                // Redirect to front page.
                this.props.history.push("/");
            });
    }

    render() {
        return (
            <div className="container">
                <h1>Edit Profile</h1>
                <hr/>
                <div className="row">
                    <div className="col-md-3">
                        <div className="text-center">
                            <div className="avatar img-circle">
                                <UserAvatar size="100" name={User.profile.name}/>
                            </div>

                            <h6>Upload a different photo...</h6>

                            <input className="form-control" type="file"/>
                        </div>
                    </div>

                    <div className="col-md-9 personal-info">
                        <div className="alert alert-info alert-dismissable"
                             style={this.state.error ? {} : {display: `none`}}>
                            <a className="panel-close close" data-dismiss="alert">×</a>
                            <i className="fa fa-coffee"/>
                            {this.state.errorText}
                        </div>
                        <h3>Personal info</h3>
                        <Form horizontal>
                            <FormGroup controlId="editProfileName" validationState={this.state.nameValidationError}>
                                <Col componentClass={ControlLabel} sm={3}>Name:</Col>
                                <Col sm={8}>
                                    <FormControl type="text" value={this.state.name}
                                                 onChange={this.handleNameChange}
                                                 onBlur={this.handleNameChangeEnd}/>
                                    <FormControl.Feedback/>
                                    {this.state.nameValidationError &&
                                    <ControlLabel>Invalid name</ControlLabel>
                                    }
                                </Col>
                            </FormGroup>
                            <FormGroup controlId="editProfileEmail" validationState={this.state.emailValidationError}>
                                <Col componentClass={ControlLabel} sm={3}>Email:</Col>
                                <Col sm={8}>
                                    <FormControl value={this.state.email} type="text"
                                                 onChange={this.handleEmailChange}
                                                 onBlur={this.handleEmailChangeEnd}/>
                                    <FormControl.Feedback/>
                                    {this.state.emailValidationError &&
                                    <ControlLabel>Invalid email</ControlLabel>
                                    }
                                </Col>
                            </FormGroup>
                            <FormGroup controlId="editProfileUsername"
                                       validationState={this.state.usernameValidationError}>
                                <Col componentClass={ControlLabel} sm={3}>Username:</Col>
                                <Col sm={8}>
                                    <FormControl value={this.state.username} type="text"
                                                 onChange={this.handleUsernameChange}
                                                 onBlur={this.handleUsernameChangeEnd}/>
                                    <FormControl.Feedback/>
                                    {this.state.usernameValidationError &&
                                    <ControlLabel>Invalid username</ControlLabel>
                                    }
                                </Col>
                            </FormGroup>
                            <FormGroup controlId="editProfileAboutMe"
                                       validationState={this.state.aboutMeValidationError}>
                                <Col componentClass={ControlLabel} sm={3}>About me:</Col>
                                <Col sm={8}>
                                    <FormControl componentClass="textarea" value={this.state.aboutMe} type="text"
                                                 onChange={this.handleAboutMeChange}
                                                 onBlur={this.handleAboutMeChangeEnd}/>
                                    <FormControl.Feedback/>
                                    {this.state.aboutMeValidationError &&
                                    <ControlLabel>Invalid AboutMe</ControlLabel>
                                    }
                                </Col>
                            </FormGroup>
                            <FormGroup controlId="editProfilePassword">
                                <Col componentClass={ControlLabel} sm={3}>Password:</Col>
                                <Col sm={8}>
                                    <FormControl value="" type="password"
                                                 onChange={this.handlePasswordChange}/>

                                </Col>
                            </FormGroup>
                            <FormGroup controlId="editProfileConfirmPassword">
                                <Col componentClass={ControlLabel} sm={3}>Confirm password:</Col>
                                <Col sm={8}>
                                    <FormControl value="" type="password"
                                                 onChange={this.handlePasswordConfirmChange}/>

                                </Col>
                            </FormGroup>
                            <FormGroup controlId="editProfileSaveOrCancel">
                                <Col componentClass={ControlLabel} sm={3}/>
                                <Col sm={8}>
                                    <Button bsClass="btn btn-primary" onClick={this.handleSubmit}>Save Changes</Button>
                                    <span>&nbsp;&nbsp;</span>
                                    <Button bsClass="btn btn-default" onClick={this.handleCancel}
                                            type="reset">Cancel</Button>
                                </Col>
                            </FormGroup>
                        </Form>
                    </div>
                </div>
            </div>
        )
    }
}

export default EditProfile;
