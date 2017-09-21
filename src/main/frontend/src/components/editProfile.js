/**
 * CSS template from https://www.bootply.com/sY7gQy6XF7
 */

import React from "react";

import User from "../util/User";

import axios from "axios";
import UserAvatar from "react-user-avatar";
import {Button, Col, ControlLabel, Form, FormControl, FormGroup, Glyphicon} from "react-bootstrap";
import InterestsTagCloud from "./interestsTagCloud";

class EditProfile extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            user: undefined,

            submitBtnEnabled: true,
            error: undefined,
            errorText: "",
            tagsEditState: false,
            tagsEditBtnIcon: "pencil",

            name: "",
            email: "",
            username: "",
            aboutMe: "",
            password: "",
            passwordConfirmation: "",

            nameValidationError: null,
            emailValidationError: null,
            usernameValidationError: null,
            aboutMeValidationError: null,
            passwordValidationError: null,
            passwordConfirmationValidationError: null

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
        this.handlePasswordChangeEnd = this.handlePasswordChange.bind(this);
        this.handlePasswordConfirmChange = this.handlePasswordConfirmChangeEnd.bind(this);
        this.handlePasswordConfirmChangeEnd = this.handlePasswordConfirmChangeEnd.bind(this);

        this.validateName = this.validateName.bind(this);
        this.validateUsername = this.validateUsername.bind(this);
        this.validateEmail = this.validateEmail.bind(this);
        this.validatePassword = this.validatePassword.bind(this);
        this.validatePasswordConfirmation = this.validatePasswordConfirmation.bind(this);

        this.handleEditTags = this.handleEditTags.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);

        this.isAllValid = this.isAllValid.bind(this);
    }

    // This function is called before render() to initialize its state.
    componentWillMount() {
        axios.get('/api/user/' + User.id)
            .then(({data}) => {
                User.name = (data.profile.name === null || undefined) ? "Unknown" : data.profile.name;
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
        this.setState({name: event.target.value.trim()}, this.validateName(this.state.name))
    }

    validateName(value) {
        // regex from https://stackoverflow.com/a/2044909/3898604
        let re = /^([ \u00c0-\u01ffa-zA-Z'\-])+$/g;
        if (!re.test(value)) {
            this.setState({nameValidationError: "error"}, this.isAllValid);
        } else {
            this.setState({nameValidationError: null}, this.isAllValid);
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
        let re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        if (!re.test(value)) {
            this.setState({emailValidationError: "error"}, this.isAllValid);
        } else {
            this.setState({emailValidationError: null}, this.isAllValid);
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
        let re = /^[a-zA-z_0-9]{3,20}$/;
        if (!re.test(value)) {
            this.setState({usernameValidationError: "error"}, this.isAllValid);
        } else {
            this.setState({usernameValidationError: null}, this.isAllValid);
        }
    }

    handleAboutMeChange(event) {
        this.setState({aboutMe: event.target.value});
    }

    handleAboutMeChangeEnd(event) {
        this.setState({aboutMe: event.target.value});
    }

    validateAboutMe(value) {

    }

    handlePasswordChange(event) {
        this.setState({password: event.target.value.trim()}, this.validatePassword(this.state.password));
    }

    handlePasswordChangeEnd(event) {
        this.setState({password: event.target.value.trim()}, this.validatePassword(this.state.password));
    }

    validatePassword(value) {
        let re = /^.{8,}$/;
        if (!re.test(value) && value.length > 0) {
            console.log("validatePassword: too short");
            this.setState({passwordValidationError: "error"}, this.isAllValid);
        } else {
            this.setState({passwordValidationError: null}, this.isAllValid);
        }
    }

    handlePasswordConfirmChange(event) {
        this.setState({passwordConfirmation: event.target.value.trim()},
            this.validatePasswordConfirmation(this.state.passwordConfirmation));
    }

    handlePasswordConfirmChangeEnd(event) {
        this.setState({passwordConfirmation: event.target.value.trim()},
            this.validatePasswordConfirmation(this.state.passwordConfirmation));
    }

    validatePasswordConfirmation(value) {
        if (this.state.password === value && value.length > 0) {
            this.setState({passwordConfirmationValidationError: "error"}, this.isAllValid);
        } else {
            this.setState({passwordConfirmationValidationError: null}, this.isAllValid);
        }
    }

    isAllValid() {
        let inputFieldsValid = (!this.state.nameValidationError &&
            !this.state.usernameValidationError &&
            !this.state.emailValidationError);
        let passwordFieldFilled = (this.state.password.length > 0 ||
            this.state.passwordConfirmation.length > 0);
        if (inputFieldsValid && !passwordFieldFilled) {
            console.log("all valid true (" + this.state.nameValidationError + "," + this.state.usernameValidationError + "," + this.state.emailValidationError + ")");
            this.setState({submitBtnEnabled: true});
        } else if (inputFieldsValid && passwordFieldFilled) {
            console.log("inputs valid password filled");
            if (this.state.password === this.state.passwordConfirmation) {
                console.log("Passwords ==");
                this.setState({
                    submitBtnEnabled: true,
                    passwordConfirmationValidationError: null
                });
            } else {
                console.log("Passwords =!");
                this.setState({
                    submitBtnEnabled: false,
                    passwordConfirmationValidationError: "error"
                });
            }
        } else {
            console.log("all valid false (" + this.state.nameValidationError + "," + this.state.usernameValidationError + "," + this.state.emailValidationError + ")");
            this.setState({submitBtnEnabled: false});
        }
    }

    handleEditTags() {
        if (this.state.tagsEditState) {
            this.setState({
                tagsEditBtnIcon: "pencil",
                tagsEditState: false
            });
        } else {
            this.setState({
                tagsEditBtnIcon: "ok",
                tagsEditState: true
            });
        }

    }

    handleSubmit(event) {
        event.preventDefault();
        axios.patch('/api/user',
            {
                email: this.state.email,
                username: this.state.username,
                profile: Object.assign({}, this.state.user.profile, {
                    name: this.state.name,
                    aboutMe: this.state.aboutMe
                }),
                password: this.state.password
            })
            .then((data) => {
                // Redirect to front page.
                console.log(data);
            });



        this.setState({
            user: Object.assign({}, this.state.user, {
                profile: Object.assign({}, this.state.user.profile, {
                    name: this.state.name,
                    aboutMe: this.state.aboutMe
                }),
                username: this.state.username,
                email: this.state.email
            }),
        });
    }

    handleCancel(event) {
        event.preventDefault();
        this.setState({
            name: (this.state.origUser.profile.name === null || undefined) ? "" : this.state.origUser.profile.name,
            email: (this.state.origUser.email === null || undefined) ? "" : this.state.origUser.email,
            username: (this.state.origUser.username === null || undefined) ? "" : this.state.origUser.username,
            aboutMe: (this.state.origUser.profile.aboutMe === null || undefined) ? "" : this.state.origUser.profile.aboutMe,
            password: "",
            passwordConfirmation: "",

            nameValidationError: null,
            emailValidationError: null,
            usernameValidationError: null,
            aboutMeValidationError: null,
            passwordValidationError: null,
            passwordConfirmationValidationError: null,


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
                        <div className="tagcloud">
                            <h3>Interests &nbsp;&nbsp;
                                <Button onClick={this.handleEditTags}><Glyphicon
                                    glyph={this.state.tagsEditBtnIcon}/></Button></h3>



                            <div className="tags">
                                <InterestsTagCloud/>
                            </div>
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
                            <FormGroup controlId="editProfileName"
                                       validationState={this.state.nameValidationError}>
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
                            <FormGroup controlId="editProfileEmail"
                                       validationState={this.state.emailValidationError}>
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
                                    <FormControl componentClass="textarea" value={this.state.aboutMe}
                                                 type="text"
                                                 rows={5}
                                                 onChange={this.handleAboutMeChange}
                                                 onBlur={this.handleAboutMeChangeEnd}/>
                                    <FormControl.Feedback/>
                                    {this.state.aboutMeValidationError &&
                                    <ControlLabel>Invalid AboutMe</ControlLabel>
                                    }
                                </Col>
                            </FormGroup>
                            <FormGroup controlId="editProfilePassword"
                                       validationState={this.state.passwordValidationError}>
                                <Col componentClass={ControlLabel} sm={3}>Password:</Col>
                                <Col sm={8}>
                                    <FormControl value={this.state.password} type="password"
                                                 onChange={this.handlePasswordChange}
                                                 onBlur={this.handlePasswordChangeEnd}
                                                 autoComplete='off'/>

                                    <FormControl.Feedback/>
                                    {this.state.passwordValidationError &&
                                    <ControlLabel>Entered password does not match requirements.</ControlLabel>
                                    }
                                </Col>
                            </FormGroup>
                            <FormGroup controlId="editProfileConfirmPassword"
                                       validationState={this.state.passwordConfirmationValidationError}>
                                <Col componentClass={ControlLabel} sm={3}>Confirm password:</Col>
                                <Col sm={8}>
                                    <FormControl value={this.state.passwordConfirmation} type="password"
                                                 onChange={this.handlePasswordConfirmChange}
                                                 onBlur={this.handlePasswordConfirmChangeEnd}/>
                                    <FormControl.Feedback/>
                                    {this.state.passwordConfirmationValidationError &&
                                    <ControlLabel>The entered passwords do not match.</ControlLabel>
                                    }
                                </Col>
                            </FormGroup>
                            <FormGroup controlId="editProfileSaveOrCancel">
                                <Col componentClass={ControlLabel} sm={3}/>
                                <Col sm={8}>
                                    <Button bsClass="btn btn-primary"
                                            disabled={!this.state.submitBtnEnabled}
                                            onClick={this.handleSubmit}>Save Changes</Button>
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
