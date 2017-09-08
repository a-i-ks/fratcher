import React from "react";

class MatchList extends React.Component {

    constructor(props) {
        super();
        this.state = {
            matches: []
        };

        this.handleClick = this.handleClick.bind(this);
    }

    // This function is called before render() to initialize its state.
    componentWillMount() {
        axios.get('/api/matches')
            .then(({data}) => {
                this.setState({
                    posts: data
                })
            });
    }

    render() {
        return (
            <div></div>
        );
    }
}

export default MatchList;