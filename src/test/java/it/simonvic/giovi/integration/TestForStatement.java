package it.simonvic.giovi.integration;

import static it.simonvic.giovi.GioviVisitorUtils.visit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TestForStatement {

	@Nested
	class PlaceholderIterator {

		@Test
		void onRange() {
			visit(
				"""
				"for _ in 1..3" -> _out
				for _ in 1..3 do "_" -> _out
				"""
			);
		}

		@Test
		void onList() {
			visit(
				"""
				"for _ in [1 2 3]" -> _out
				for _ in [1 2 3] do "_" -> _out
				"""
			);
		}

		@Test
		void onMap() {
			visit(
				"""
				'for _ in ["a":1 "b":2 "c":3]' -> _out
				for _ in ["a":1 "b":2 "c":3] do "_" -> _out
				"""
			);
		}

	}

	@Nested
	class UnaryIterator {

		@Test
		void onRange() {
			visit(
				"""
				'for i in 1..3' -> _out
				for i in 1..3 do i -> _out
				"""
			);
		}

		@Test
		void onList() {
			visit(
				"""
				'for i in [1 2 3]' -> _out
				for i in [1 2 3] do i -> _out
				"""
			);
		}

		@Test
		void onMap() {
			visit(
				"""
				'for k in ["a":1 "b":2 "c":3]' -> _out
				for k in ["a":1 "b":2 "c":3] do k -> _out
				"""
			);
		}

	}

	@Nested
	class MapIterator {

		@Test
		void onMap_withPlaceholderKeyAndPlaceholderValue() {
			visit(
				"""
				'for _ : _ in ["a":1 "b":2 "c":3]' -> _out
				for _ : _ in ["a":1 "b":2 "c":3] do "_ : _" -> _out
				"""
			);
		}

		@Test
		void onMap_withKeyAndPlaceholderValue() {
			visit(
				"""
				'for k : _ in ["a":1 "b":2 "c":3]' -> _out
				for k : _ in ["a":1 "b":2 "c":3] do "$k : _" -> _out
				"""
			);
		}

		@Test
		void onMap_withPlaceholderKeyAndValue() {
			visit(
				"""
				'for _ : v in ["a":1 "b":2 "c":3]' -> _out
				for _ : v in ["a":1 "b":2 "c":3] do "_ : $v" -> _out
				"""
			);
		}

		@Test
		void onMap_withKeyValue() {
			visit(
				"""
				'for k : v in ["a":1 "b":2 "c":3]' -> _out
				for k : v in ["a":1 "b":2 "c":3] do "$k : $v" -> _out
				"""
			);
		}

	}

	@Nested
	class ProductIterator {

		@Test
		void onList() {
			visit(
				"""
				Foo<b Bool, i Int, t Text>
				list [Foo] <- [<false, 0, "no">, <true, 1, "yes">]
				"for <b, i, t> in $list" -> _out
				for <b, i, t> in list do "$b $i $t" -> _out
				"""
			);
		}

		@Test
		void onList_withWhole() {
			visit(
				"""
				Foo<b Bool, i Int, t Text>
				list [Foo] <- [<false, 0, "no">, <true, 1, "yes">]
				"for w <b, i, t> in $list" -> _out
				for w <b, i, t> in list do "$w    $b $i $t" -> _out
				"""
			);
		}

		@Test
		void onList_withSubsetOfFields() {
			visit(
				"""
				Foo<b Bool, i Int, t Text>
				list [Foo] <- [<false, 0, "no">, <true, 1, "yes">]
				"for <t, b> in $list" -> _out
				for <t, b> in list do "$t $b" -> _out
				"""
			);
		}

		@Test
		void onListOfNestedStructs() {
			visit(
				"""
				Bar<i Int, n Natural>
				Foo<b Bool, bar Bar, t Text>
				list [Foo] <- [<false, <0i, 0n>, "no">, <true, <1i, 1n>, "yes">]
				"for <b, bar, t> in $list" -> _out
				for <b, bar, t> in list do "$b $bar $t" -> _out
				"""
			);
		}

	}


	@Nested
	class NestedIterators {

		@Disabled("Not implemented yet")
		@Test
		void onNestedList() {
			visit(
				"""
				nestedList [[[Int]]] <- [
					[
						[1 0 0]
						[0 1 0]
						[0 0 1]
					]
					[
						[2 0 0]
						[0 2 0]
						[0 0 2]
					]
				]
				"for i in $nestedList" -> _out
				for i in nestedList do i -> _out
				"for [i] in $nestedList" -> _out
				for [i] in nestedList do i -> _out
				"for [[i]] in $nestedList" -> _out
				for [[i]] in nestedList do i -> _out
				"for [[[i]]] in $nestedList" -> _out
				for [[[i]]] in nestedList do i -> _out
				"""
			);
		}

		@Test
		void onNestedMaps() {
			visit(
				"""
				graph [Text : [Text : Int]] <- [
					"a" : ["x":1 "y":2 "z":3]
					"b" : ["X":10 "Y":20 "Z":30]
				]
				"for u : [v : cost] in $graph" -> _out
				for u : [v : cost] in graph do "$u --$cost--> $v" -> _out
				"""
			);
		}

		@Disabled("Not implemented yet")
		@Test
		void onListOfNestedMaps() {
			visit(
				"""
				graphs [[Text : [Text : Int]]] <- [
					[
						"a" : ["x":1 "y":2 "z":3]
						"b" : ["X":10 "Y":20 "Z":30]
					]
					[
						"a" : ["x":1 "y":2 "z":3]
						"b" : ["X":10 "Y":20 "Z":30]
					]
				]
				"for [[u : [v : cost]]] in $graphs" -> _out
				for [[u : [v : cost]]] in graphs do "$u --$cost--> $v" -> _out
				"""
			);
		}

		@Test
		void onListOfNestedStructs() {
			visit(
				"""
				Bar<i Int, n Natural>
				Foo<b Bool, bar Bar, t Text>
				list [Foo] <- [<false, <0i, 0n>, "no">, <true, <1i, 1n>, "yes">]
				"for <b, bar <i, n>, t> in $list" -> _out
				for <b, bar <i, n>, t> in list do "<$b, $bar <$i, $n>, $t>" -> _out
				"""
			);
		}

		@Disabled("Not implemented yet")
		@Test
		void onNestedListOfNestedStruct() {
			visit(
				"""
				Foo<a Int, b Int, c Int>
				Bar<x Int, y Int, z Foo>
				matrixOfNestedStruct [[Bar]] <- [
					[Bar.<1, 1, Foo.<1, 1, 1>>, Bar.<2, 2, Foo.<2, 2, 2>>, Bar.<3, 3, Foo.<3, 3, 3>>]
					[Bar.<1, 1, Foo.<1, 1, 1>>, Bar.<2, 2, Foo.<2, 2, 2>>, Bar.<3, 3, Foo.<3, 3, 3>>]
				]
				"for [[<x, y, z <a, b, c>>]] in $matrixOfNestedStruct" -> _out
				for [[<x, y, z <a, b, c>>]] in matrixOfNestedStruct do "<$x $y $z <$a $b $c>>" -> _out
				"""
			);
		}

		@Test
		void onMap_withStructKey() {
			visit(
				"""
				Bar<i Int, n Natural>
				Foo<b Bool, bar Bar, t Text>
				map [Foo : Text] <- [
					<false, <0i, 0n>, "no"> : "a"
					<true, <1i, 1n>, "yes"> : "b"
				]
				"for <b, bar <i, n>, t> in $map" -> _out
				for k <b, bar <i, n>, t> : v in map do "$k <$b, $bar <$i, $n>, $t> : $v" -> _out
				"""
			);
		}

		@Test
		void onMap_withStructValue() {
			visit(
				"""
				Bar<i Int, n Natural>
				Foo<b Bool, bar Bar, t Text>
				map [Text : Foo] <- [
					"a" : <false, <0i, 0n>, "no">,
					"b" : <true, <1i, 1n>, "yes">
				]
				"for <b, bar <i, n>, t> in $map" -> _out
				for k : v <b, bar <i, n>, t> in map do "$k : $v <$b, $bar <$i, $n>, $t>" -> _out
				"""
			);
		}

		@Test
		void onMap_withStructKeyValue() {
			visit(
				"""
				Bar<i Int, n Natural>
				Foo<b Bool, bar Bar, t Text>
				map [Foo : Bar] <- [
					<false, <0i, 0n>, "no"> : <0i, 0n>
					<true, <1i, 1n>, "yes"> : <1i, 1n>
				]
				"for <b, bar, t> : <i, n> in $map" -> _out
				for <b, bar, t> : <i, n> in map do "<$b, $bar, $t> : <$i, $n>" -> _out
				"""
			);
		}


	}


}
