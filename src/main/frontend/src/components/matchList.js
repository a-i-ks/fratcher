import React from "react";

import User from "../util/User";

import UserAvatar from "react-user-avatar";
import axios from "axios";

class MatchList extends React.Component {

    constructor(props) {
        super();
        this.state = {
            matches: [],
            loading: true,
            error: null
        };

        this.handleClick = this.handleClick.bind(this);
    }

    renderLoading() {
        return <div>Loading...</div>;
    }

    handleClick(id) {
        this.props.history.push(`/matches/${id}`);
    }

    // This function is called before render() to initialize its state.
    componentWillMount() {
        axios.get('/api/user/match')
            .then(({data}) => {

                this.setState({
                    matches: data,
                    loading: false,
                    error: null
                });
                this.forceUpdate();
            })
            .catch(({error}) => {
                console.log("error during load");
                this.setState({
                    loading: false,
                    error: error
                });
            });
    }

    renderError() {
        return (
            <tr>
                <td>An error has occurred: {this.state.error.message}</td>
            </tr>
        );
    }

    renderMatches() {

        const Match = ({match}) => {
            let user = null;
            const {user1, user2} = match;
            if (user1.id === User.id) {
                user = user2;
            } else {
                user = user1;
            }
            const {profile} = user;
            const {name, imgPath} = profile;

            return (
                <div key={match.id} onClick={() => this.handleClick(match.id)}>
                    <div>
                        {imgPath &&
                        <UserAvatar size="40" name={name} src={imgPath}/>}
                        {!imgPath &&
                        <UserAvatar size="40" name={name}/>}
                    </div>
                    <div>
                        {name}
                    </div>
                </div>
            );
        };

        // Destructuring error and matches from state.
        // => I don't have to write "this.state...." every time.
        const {error, matches} = this.state;

        if (error) {
            return this.renderError();
        }

        if (matches.isEmpty) {
            return (<div>No matches found!</div>)
        }


        return (
            <div>
                {matches.map(match =>
                    <Match key={match.id} match={match}/>
                )}
            </div>
        );
    }

    render() {
        const {loading} = this.state;

        const candidateDivStyle = {
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))'
        };

        return (
            <div className="component">
                {loading ? this.renderLoading() :
                    <div style={candidateDivStyle}>
                        {this.renderMatches()}
                    </div>}
            </div>
        );
    }
}

export default MatchList;