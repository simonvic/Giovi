package it.simonvic.giovi.integration;

import static it.simonvic.giovi.GioviVisitorUtils.visit;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TestForStatement {

	@Nested
	class PlaceholderPattern {

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
				'for _ in ["a"=>1 "b"=>2 "c"=>3]' -> _out
				for _ in ["a"=>1 "b"=>2 "c"=>3] do "_" -> _out
				"""
			);
		}

	}

	@Nested
	class UnaryPattern {

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
				'for k in ["a"=>1 "b"=>2 "c"=>3]' -> _out
				for k in ["a"=>1 "b"=>2 "c"=>3] do k -> _out
				"""
			);
		}

	}

	@Nested
	class MapPattern {

		@Test
		void onMap_withPlaceholderKeyAndPlaceholderValue() {
			visit(
				"""
				'for _ => _ in ["a"=>1 "b"=>2 "c"=>3]' -> _out
				for _ => _ in ["a"=>1 "b"=>2 "c"=>3] do "_ => _" -> _out
				"""
			);
		}

		@Test
		void onMap_withKeyAndPlaceholderValue() {
			visit(
				"""
				'for k => _ in ["a"=>1 "b"=>2 "c"=>3]' -> _out
				for k => _ in ["a"=>1 "b"=>2 "c"=>3] do "$k => _" -> _out
				"""
			);
		}

		@Test
		void onMap_withPlaceholderKeyAndValue() {
			visit(
				"""
				'for _ => v in ["a"=>1 "b"=>2 "c"=>3]' -> _out
				for _ => v in ["a"=>1 "b"=>2 "c"=>3] do "_ => $v" -> _out
				"""
			);
		}

		@Test
		void onMap_withKeyValue() {
			visit(
				"""
				'for k => v in ["a"=>1 "b"=>2 "c"=>3]' -> _out
				for k => v in ["a"=>1 "b"=>2 "c"=>3] do "$k => $v" -> _out
				"""
			);
		}

	}

	@Nested
	class ProductPattern {

		@Test
		void positional_onList() {
			visit(
				"""
				struct Foo<b:Bool, i:Int, t:Text>
				list:[Foo] <- [<false, 1, "no">, <true, 2, "yes">]
				"for <b, i, t> in $list" -> _out
				for <b, i, t> in list do "$b $i $t" -> _out
				"""
			);
		}

		@Test
		void namedImplicit_onList() {
			visit(
				"""
				struct Foo<b:Bool, i:Int, t:Text>
				list:[Foo] <- [<false, 1, "no">, <true, 2, "yes">]
				"for <_=b, _=i, _=t> in $list" -> _out
				for <_=b, _=i, _=t> in list do "$b $i $t" -> _out
				"""
			);
		}

		@Test
		void namedExplicit_onList() {
			visit(
				"""
				struct Foo<b:Bool, i:Int, t:Text>
				list:[Foo] <- [<false, 1, "no">, <true, 2, "yes">]
				"for <b=b1, i=i1, t=t1> in $list" -> _out
				for <b=b1, i=i1, t=t1> in list do "$b1 $i1 $t1" -> _out
				"""
			);
		}

		@Test
		void namedMixed_onList() {
			visit(
				"""
				struct Foo<b:Bool, i:Int, t:Text>
				list:[Foo] <- [<false, 1, "no">, <true, 2, "yes">]
				"for <b=b1, _=i, t=t> in $list" -> _out
				for <b=b1, _=i, t=t> in list do "$b1 $i $t" -> _out
				"""
			);
		}

		@Test
		void positional_onList_withWhole() {
			visit(
				"""
				struct Foo<b:Bool, i:Int, t:Text>
				list:[Foo] <- [<false, 1, "no">, <true, 2, "yes">]
				"for w <b, i, t> in $list" -> _out
				for w <b, i, t> in list do "$w    $b $i $t" -> _out
				"""
			);
		}

		@Test
		void namedImplicit_onList_withWhole() {
			visit(
				"""
				struct Foo<b:Bool, i:Int, t:Text>
				list:[Foo] <- [<false, 1, "no">, <true, 2, "yes">]
				"for w <_=b, _=i, _=t> in $list" -> _out
				for w <_=b, _=i, _=t> in list do "$w    $b $i $t" -> _out
				"""
			);
		}

		@Test
		void namedExplicit_onList_withWhole() {
			visit(
				"""
				struct Foo<b:Bool, i:Int, t:Text>
				list:[Foo] <- [<false, 1, "no">, <true, 2, "yes">]
				"for w <b=b1, i=i1, t=t1> in $list" -> _out
				for w <b=b1, i=i1, t=t1> in list do "$w    $b1 $i1 $t1" -> _out
				"""
			);
		}

		@Test
		void positional_onList_withSubsetOfFields() {
			visit(
				"""
				struct Foo<b:Bool, i:Int, t:Text>
				list:[Foo] <- [<false, 1, "no">, <true, 2, "yes">]
				"for <b, _, t> in $list" -> _out
				for <b, _, t> in list do "$b $t" -> _out
				"""
			);
		}

		@Test
		void named_onList_withSubsetOfFields() {
			visit(
				"""
				struct Foo<b:Bool, i:Int, t:Text>
				list:[Foo] <- [<false, 1, "no">, <true, 2, "yes">]
				"for <_=t, _=b> in $list" -> _out
				for <_=t, _=b> in list do "$b $t" -> _out
				"""
			);
		}

		@Test
		void positional_onListOfNestedStructs() {
			visit(
				"""
				struct Bar<i:Int, n:Natural>
				struct Foo<b:Bool, bar:Bar, t:Text>
				list:[Foo] <- [<false, <1i, 2n>, "no">, <true, <10i, 20n>, "yes">]
				"for <b, bar, t> in $list" -> _out
				for <b, bar, t> in list do "$b $bar $t" -> _out
				"""
			);
		}

	}


	@Nested
	class NestedPatterns {

		@Test
		void onNestedList() {
			visit(
				"""
				nestedList:[[[Int]]] <- [
					[
						[1      2      3]
						[10     20     30]
						[100    200    300]
					]
					[
						[1000   2000   3000]
						[10000  20000  30000]
						[100000 200000 300000]
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
				graph:[Text => [Text => Int]] <- [
					"a" => ["x"=>1 "y"=>2 "z"=>3]
					"b" => ["u"=>10 "v"=>20 "w"=>30]
				]
				"for u => [v => cost] in $graph" -> _out
				for u => [v => cost] in graph do "$u --$cost--> $v" -> _out
				"""
			);
		}

		@Test
		void onMapOfLists() {
			visit(
				"""
				map:[Text => [Int]] <- [
					"a" => [1 2 3]
					"b" => [10 20 30]
				]
				"for u => v in $map" -> _out
				for u => v in map do "$u => $v" -> _out
				"for u => [v] in $map" -> _out
				for u => [v] in map do "$u => $v" -> _out
				"""
			);
		}


		@Test
		void onListOfNestedMaps() {
			visit(
				"""
				graphs:[[Text => [Text => Int]]] <- [
					[
						"a" => ["x"=>1 "y"=>2 "z"=>3]
						"b" => ["u"=>10 "v"=>20 "w"=>30]
					]
					[
						"A" => ["X"=>100 "Y"=>200 "Z"=>300]
						"B" => ["U"=>1000 "V"=>2000 "W"=>3000]
					]
				]
				"for [[u => [v => cost]]] in $graphs" -> _out
				for [[u => [v => cost]]] in graphs do "$u --$cost--> $v" -> _out
				"""
			);
		}

		@Test
		void onListOfNestedStructs_positional() {
			visit(
				"""
				struct Bar<i:Int, n:Natural>
				struct Foo<b:Bool, bar:Bar, t:Text>
				list:[Foo] <- [<false, <0i, 0n>, "no">, <true, <1i, 1n>, "yes">]
				"for <b, bar <i, n>, t> in $list" -> _out
				for <b, bar <i, n>, t> in list do "<$b, $bar <$i, $n>, $t>" -> _out
				"""
			);
		}

		@Test
		void onListOfNestedStructs_named() {
			visit(
				"""
				struct Bar<i:Int, n:Natural>
				struct Foo<b:Bool, bar:Bar, t:Text>
				list:[Foo] <- [<false, <0i, 0n>, "no">, <true, <1i, 1n>, "yes">]
				"for <_=b, bar=bar<_=i, _=n>, _=t> in $list" -> _out
				for <_=b, bar=bar<_=i, _=n>, _=t> in list do "<$b, $bar <$i, $n>, $t>" -> _out
				"""
			);
		}

		@Test
		void onNestedListOfNestedStruct() {
			visit(
				"""
				struct Foo<a:Int, b:Int, c:Int>
				struct Bar<x:Int, y:Int, z:Foo>
				matrixOfNestedStruct:[[Bar]] <- [
					[
						Bar.<1,      2,      Foo.<3,      4,      5>>,
						Bar.<10,     20,     Foo.<30,     40,     50>>,
						Bar.<100,    200,    Foo.<300,    400,    500>>,
					]
					[
						Bar.<1000,   2000,   Foo.<3000,   4000,   5000>>,
						Bar.<10000,  20000,  Foo.<30000,  40000,  50000>>,
						Bar.<100000, 200000, Foo.<300000, 400000, 500000>>,
					]
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
				struct Bar<i:Int, n:Natural>
				struct Foo<b:Bool, bar:Bar, t:Text>
				map:[Foo => Text] <- [
					<false, <1i, 2n>, "no"> => "a"
					<true, <10i, 20n>, "yes"> => "b"
				]
				"for <b, bar <i, n>, t> in $map" -> _out
				for k <b, bar <i, n>, t> => v in map do "$k <$b, $bar <$i, $n>, $t> => $v" -> _out
				"""
			);
		}

		@Test
		void onMap_withStructValue() {
			visit(
				"""
				struct Bar<i:Int, n:Natural>
				struct Foo<b:Bool, bar:Bar, t:Text>
				map:[Text => Foo] <- [
					"a" => <false, <1i, 2n>, "no">,
					"b" => <true, <10i, 20n>, "yes">
				]
				"for <b, bar <i, n>, t> in $map" -> _out
				for k => v <b, bar <i, n>, t> in map do "$k => $v <$b, $bar <$i, $n>, $t>" -> _out
				"""
			);
		}

		@Test
		void onMap_withStructKeyValue() {
			visit(
				"""
				struct Bar<i:Int, n:Natural>
				struct Foo<b:Bool, bar:Bar, t:Text>
				map:[Foo => Bar] <- [
					<false, <1i, 2n>, "no"> => <3i, 4n>
					<true, <10i, 20n>, "yes"> => <30i, 40n>
				]
				"for <b, bar, t> => <i, n> in $map" -> _out
				for <b, bar, t> => <i, n> in map do "<$b, $bar, $t> => <$i, $n>" -> _out
				"""
			);
		}

		@Test
		void onCrazy() {
			visit(
				"""
				# 3d matrix of maps of nested maps of list of foo LMAO
				struct Bar<i:Int, n:Natural>
				struct Foo<b:Bool, bar:Bar, t:Text>
				crazy:[[[[Text => [Text => [Foo]]]]]] <- [
					[
						[
							[
								"a" => [
									"aa" => [
										<false, <0i, 0n>, "no">,
										<true, <1i, 1n>, "yes">
									]
									"ab" => [
										<false, <0i, 0n>, "no">,
										<true, <1i, 1n>, "yes">
									]
								]
							]
							[
								"b" => [
									"ba" => [
										<false, <0i, 0n>, "no">,
										<true, <1i, 1n>, "yes">
									]
									"bb" => [
										<false, <0i, 0n>, "no">,
										<true, <1i, 1n>, "yes">
									]
								]
							]
						]
						[
							[
								"A" => [
									"Aa" => [
										<false, <0i, 0n>, "no">,
										<true, <1i, 1n>, "yes">
									]
									"Ab" => [
										<false, <0i, 0n>, "no">,
										<true, <1i, 1n>, "yes">
									]
								]
							]
							[
								"B" => [
									"Ba" => [
										<false, <0i, 0n>, "no">,
										<true, <1i, 1n>, "yes">
									]
									"Bb" => [
										<false, <0i, 0n>, "no">,
										<true, <1i, 1n>, "yes">
									]
								]
							]
						]
					]
				]
				for [[[[k => [kk => [<b, bar, t>]]]]]] in crazy do "[[[$k => [$kk => [<$b, $bar, $t>]]]]]" -> _out
				"""
			);
		}

	}


}
