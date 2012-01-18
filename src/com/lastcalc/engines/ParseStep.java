package com.lastcalc.engines;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.*;
import com.lastcalc.parsers.Parser.ParseResult;
import com.lastcalc.parsers.PreParser.ListWithTail;
import com.lastcalc.parsers.PreParser.MapWithTail;
import com.lastcalc.parsers.UserDefinedParserParser.UserDefinedParser;

public class ParseStep implements Comparable<ParseStep> {
	public final TokenList input;

	public final ParseResult result;

	public final Parser parser;

	public final ParseStep previous;

	public final int depth;

	public final int createOrder;

	public final double scoreBias;

	public ParseStep(final TokenList input, final Parser parser, final ParseResult result, final ParseStep previous,
			final int createOrder, final double score) {
		this.input = input;
		this.parser = parser;
		scoreBias = (previous == null ? 0 : previous.scoreBias) + score;
		this.result = result;
		this.previous = previous;
		this.createOrder = createOrder;
		if (previous != null) {
			depth = previous.depth + 1;
		} else {
			depth = 0;
		}
	}

	@Override
	public String toString() {
		return input + " -> " + parser.getClass().getSimpleName() + " : " + parser + " -> " + result.output;
	}

	@Override
	public int compareTo(final ParseStep other) {
		if (getScore() < other.getScore())
			return -1;
		else if (getScore() > other.getScore())
			return 1;
		else if (hashCode() < other.hashCode())
			return -1;
		else if (hashCode() > other.hashCode())
			return 1;
		else
			return 0;
	}

	double cachedScore = Double.MIN_VALUE;

	private double getScore() {
		return scoreBias + getScore(result.output);
	}

	public static double getScore(final Object token) {
		if (token instanceof TokenList) {
			double ret = 0;
			for (final Object t : ((TokenList) token)) {
				ret += getScore(t);
			}
			return ret;
		} else if (token instanceof UserDefinedParser) {
			double ret = 0;
			for (final Object t : ((UserDefinedParser) token).after) {
				ret += getScore(t);
			}
			return ret;
		} else if (token instanceof ListWithTail) {
			final ListWithTail lwt = (ListWithTail) token;
			return 2 + getScore(lwt.list) + getScore(lwt.tail);
		} else if (token instanceof MapWithTail) {
			final MapWithTail mwt = (MapWithTail) token;
			return 2 + getScore(mwt.map) + getScore(mwt.tail);
		} else
			return 1;
	}

	public boolean isMinimal() {
		if (result.output.size() > 1)
			return false;
		if (result.output.get(0) instanceof ListWithTail || result.output.get(0) instanceof MapWithTail)
			return false;
		if (result.output.get(0) instanceof UserDefinedParser)
			return ((UserDefinedParser) result.output.get(0)).after.size() == 1;
		return true;
	}
}