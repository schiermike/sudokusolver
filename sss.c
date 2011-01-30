#include <stdio.h>
#include <string.h>

/*
 SIMPLE SUDOKU SOLVER

 good reference: http://www.sudokuwiki.org
 SUDOKUS:
 hidden-triple: 38.......97.21....6..583...2...5.9..5..621..3..8.....5...435..2...19..56.5......1
 moderate: 4...1.......3.9.4..7...5..9....6..21..4.7.6..19..5....9..4...7..3.6.8.......3...6
 boxline:  .3621.84.8...45631.14863..9287.3.456693584...1456723984.8396...35..28.64.6.45..83
 linebox:  7..921483..95...6...8...5...8.453.76..7...8..5..7...4...3.......2....7..87.195...
*/

// === COMMON STUFF ===================================================================================

int s[81];
int* row[9][9];
int* col[9][9];
int* box[9][9];

int mask[10] = {511, 1, 2, 4, 8, 16, 32, 64, 128, 256};

/* box-order:
  123
  456
  789
*/

// check whether there is only one value in this cell (whether it is fixed)
int is_fixed(int v) {
	int i;
	for(i=1; i<=9; i++)
		if(v == mask[i])
			return 1;
	return 0;
}

// returns a mask which covers all fixed values of the structure
int get_fixed_mask(int* struc[9]) {
	int mask = 0;
	int i;
	for(i=0; i<9; i++)
		if(is_fixed(*struc[i]))
			mask |= *struc[i];
	return mask;
}

// count how many values are still possible for this cell
int count(int v) {
	int i;
	int c = 0;
	for(i=1; i<=9; i++)
		if(v & mask[i])
			c++;
	return c;
}

int is_solved() {
	int i;
	for(i=0; i<81; i++)
	if(!is_fixed(s[i]))
		return 0;
	printf("solved!\n");
	return 1;
}

int initialize(char* input) {
	if(strlen(input) != 81)
		return -1;

	int i;
	for(i=0; i<81; i++) {
		if(input[i] == '.')
			s[i] = mask[0];
		else if(input[i] >= '1' && input[i] <= '9')
			s[i] = mask[input[i]-'0'];
		else
			return -1;
	}

	int j;
	for(i=0; i<9; i++)
		for(j=0; j<9; j++) {
			int* addr = s + i*9 + j;
			row[i][j] = addr;
			col[j][i] = addr;
			box[(i/3)*3+j/3][(i%3)*3+j%3] = addr;
		}

	return 0;
}

void wait()
{
	getc(stdin);
}

// === PRINTING ===================================================================================

void print_row(int* row[]) {
	int i,j;
	for(i=0; i<9; i++) {
		if(i == 3 || i == 6)
			printf("#");
		for(j=1; j<=9; j++)
			if(*row[i] & mask[j] /*&& !is_fixed(*row[i])*/)
				printf("%d", j);
			else
				printf(" ");
		printf(i%3 == 2 ? "" : "|");
	}
	printf("\n");
}

void print_sudoku() {
	int i;
	printf("#########################################################################################\n");
	for(i=0; i<9; i++) {
		if(i%3==0)
				printf(i==0?"":"=========================================================================================\n");
		else
			printf("---------+---------+---------#---------+---------+---------#---------+---------+---------\n");
		print_row(row[i]);
	}
}

// === NAKED SINGLE ===================================================================================

// if there are fixed values in a structure (row, col or box), remove them from non-fixed cells
int simplify_nakedsingle() {
	int i;
	for(i=0; i<9; i++)
		if(simplify_nakedsingle_struc(row[i])) {
			printf("nakedsingle(row%d)\n",i+1);
			return 1;
		}
	for(i=0; i<9; i++)
		if(simplify_nakedsingle_struc(col[i])) {
			printf("nakedsingle(col%d)\n", i+1);
			return 1;
		}
	for(i=0; i<9; i++)
		if(simplify_nakedsingle_struc(box[i])) {
			printf("nakedsingle(box%d)\n", i+1);
			return 1;
		}
	return 0;
}

int simplify_nakedsingle_struc(int* struc[9]) {
	int progress = 0;
	int i,j;
	for(i=0; i<9; i++)
		if(is_fixed(*struc[i]))
			for(j=0; j<9; j++)
				if(i!=j && (*struc[j] & *struc[i])) {
					*struc[j] -= *struc[i];
					progress = 1;
				}

	return progress;
}

// === HIDDEN SINGLE ===================================================================================

// try to find unique entries per structure
int simplify_hiddensingle() {
	int i;
	for(i=0; i<9; i++)
		if(simplify_hiddensingle_struc(row[i])) {
			printf("hiddensingle(row%d)\n",i+1);
			return 1;
		}
	for(i=0; i<9; i++)
		if(simplify_hiddensingle_struc(col[i])) {
			printf("hiddensingle(col%d)\n", i+1);
			return 1;
		}
	for(i=0; i<9; i++)
		if(simplify_hiddensingle_struc(box[i])) {
			printf("hiddensingle(box%d)\n", i+1);
			return 1;
		}
	return 0;
}

int simplify_hiddensingle_struc(int* struc[9]) {
	int progress = 0;
	int n, i;
	for(n=1; n<=9; n++) {
		int count = 0;
		for(i=0; i<9; i++) {
			if(*struc[i] == mask[n]) { // entry n is already fixed
				count = 9;
				break;
			}
			if(*struc[i] & mask[n]) // entry n is possible for this cell
				count++;
		}
		if(count == 1) // unique entry
			for(i=0; i<9; i++)
				if(*struc[i] & mask[n]) {
					*struc[i] = mask[n]; // entry n only occurs here -> it is now fixed
					progress = 1;
					break;
				}
	}
		
	return progress;
}

// === NAKED PAIR ===================================================================================

int simplify_nakedpair() {
	int i;
	for(i=0; i<9; i++)
		if(simplify_nakedpair_struc(row[i])) {
			printf("nakedpair(row%d)\n",i+1);
			return 1;
		}
	for(i=0; i<9; i++)
		if(simplify_nakedpair_struc(col[i])) {
			printf("nakedpair(col%d)\n", i+1);
			return 1;
		}
	for(i=0; i<9; i++)
		if(simplify_nakedpair_struc(box[i])) {
			printf("nakedpair(box%d)\n", i+1);
			return 1;
		}
	return 0;
}

int simplify_nakedpair_struc(int* struc[9]) {
	int progress = 0;
	int i,j,k;
	for(i=0; i<9; i++)
		if(count(*struc[i]) == 2)
			for(j=i+1; j<9; j++)
				if(*struc[i] == *struc[j]) { // we have found a nakedpair - can we use it?
					for(k=0; k<9; k++)
						if(i!=k && j!=k && (*struc[k] & *struc[i])) {
							*struc[k] = *struc[k] & ~*struc[i]; // flip the corresponding bits to zero
							progress = 1;
						}
					break;
				}

	return progress;
}

// === NAKED TRIPLE ===================================================================================

int simplify_nakedtriple() {
	int i;
	for(i=0; i<9; i++)
		if(simplify_nakedtriple_struc(row[i])) {
			printf("nakedtriple(row%d)\n",i+1);
			return 1;
		}
	for(i=0; i<9; i++)
		if(simplify_nakedtriple_struc(col[i])) {
			printf("nakedtriple(col%d)\n", i+1);
			return 1;
		}
	for(i=0; i<9; i++)
		if(simplify_nakedtriple_struc(box[i])) {
			printf("nakedtriple(box%d)\n", i+1);
			return 1;
		}
	return 0;
}

int simplify_nakedtriple_struc(int* struc[9]) {
	int progress = 0;
	int i1,i2,i3,k;
	for(i1=0; i1<9; i1++) {
		if(count(*struc[i1]) != 3)
			continue;
		for(i2=i1+1; i2<9; i2++) {
			if(*struc[i1] != *struc[i2])
				continue;
			for(i3=i2+1; i3<9; i3++) {
				if(*struc[i1] != *struc[i3])
					continue;
				// we found a triple, now remove these numbers from the remaining cells
				for(k=0; k<9; k++) {
					if(i1==k || i2==k || i3==k)
						continue;
					if(*struc[k] & *struc[i1]) {
						*struc[k] = *struc[k] & ~*struc[i1]; // flip the corresponding bits to zero
						progress = 1;
					}
				}
			}
		}
	}

	return progress;
}

// === HIDDEN PAIR ===================================================================================

int simplify_hiddenpair() {
	int i;
	for(i=0; i<9; i++)
		if(simplify_hiddenpair_struc(row[i])) {
			printf("hiddenpair(row%d)\n",i+1);
			return 1;
		}
	for(i=0; i<9; i++)
		if(simplify_hiddenpair_struc(col[i])) {
			printf("hiddenpair(col%d)\n", i+1);
			return 1;
		}
	for(i=0; i<9; i++)
		if(simplify_hiddenpair_struc(box[i])) {
			printf("hiddenpair(box%d)\n", i+1);
			return 1;
		}
	return 0;
}

int simplify_hiddenpair_struc(int* struc[9]) {
	int progress = 0;
	// first build mask from fixed values
	int fixed = get_fixed_mask(struc);

	// then check for all pairs not part of this mask
	int n1, n2;
	for(n1=1; n1<=9;n1++) {
		if(mask[n1] & fixed) // n1 already fixed for this row
			continue;
		for(n2=n1+1; n2<=9; n2++) {
			if(mask[n2] & fixed) // n2 already fixed for this row
				continue;
			// now check whether both values occur exactly twice in the same cells
			int c = 0;
			int mask12 = mask[n1] | mask[n2];
			int i;
			for(i=0; i<9; i++)
				if( (*struc[i] & mask12) == mask12) // both numbers occur
					c++;
				else if( (*struc[i] & mask12) != 0) { // one of both numbers occurs -> no hidden pair
					c = 9;
					break;
				}
			if(c == 2) // we have exactly two occurences of the hidden pair
				for(i=0; i<9; i++)
					if(*struc[i] != mask12 && (*struc[i] & mask12) == mask12 ) {
						*struc[i] = mask12; // for the two cells containing the hidden pair, no other candidates are possible
						progress = 1;
					}
		}
	}
		
	

	return progress;
}

// === HIDDEN TRIPLE ===================================================================================

int simplify_hiddentriple() {
	int i;
	for(i=0; i<9; i++)
		if(simplify_hiddentriple_struc(row[i])) {
			printf("hiddentriple(row%d)\n",i+1);
			return 1;
		}
	for(i=0; i<9; i++)
		if(simplify_hiddentriple_struc(col[i])) {
			printf("hiddentriple(col%d)\n", i+1);
			return 1;
		}
	for(i=0; i<9; i++)
		if(simplify_hiddentriple_struc(box[i])) {
			printf("hiddentriple(box%d)\n", i+1);
			return 1;
		}
	return 0;
}

int simplify_hiddentriple_struc(int* struc[9]) {
	int progress = 0;
	// first build mask from fixed values
	int fixed = get_fixed_mask(struc);

	// then check for all triples not part of this mask
	int n1, n2, n3;
	for(n1=1; n1<=9;n1++) {
		if(mask[n1] & fixed) // n1 already fixed for this row
			continue;
		for(n2=n1+1; n2<=9; n2++) {
			if(mask[n2] & fixed) // n2 already fixed for this row
				continue;
			for(n3=n2+1; n3<=9; n3++) {
				if(mask[n3] & fixed) // n3 already fixed for this row
					continue;
				int mask123 = mask[n1] | mask[n2] | mask[n3];
				// applying (x and mask) to all nine cells should be true exactly three times
				int c = 0;
				int i;
				for(i=0; i<9; i++)
					if(*struc[i] & mask123)
						c++;
				if(c != 3)
					continue;
				for(i=0; i<9; i++)
					if( (*struc[i] & mask123) && (*struc[i] & ~mask123) ) {
						*struc[i] &= mask123; // for the three cells containing the hidden triple, no other candidates are possible
						progress = 1;
					}
			}
		}
	}
		
	

	return progress;
}

// === BOX-LINE REDUCTION ===================================================================================

// numbers only in this box of the line -> remove from all other box entries not in the line
int simplify_intersection() {
	int l, i;
	int non_intersection_indices[6]; // indices not in the intersection
	
	for(l=0; l<9; l++) // iterate over rows -> reduce boxes
		for(i=0; i<3; i++) {
			int j,k=0;
			for(j=0; j<9; j++)
				if(j/3 != l%3)
					non_intersection_indices[k++]=j; // box indices

			if(simplify_intersection_struc(box[(l/3)*3+i], row[l], i*3, non_intersection_indices)) {
				printf("simplify_intersection_reduce_box(box%d,row%d)\n", (l/3)*3+i+1, l+1);
				return 1;
			}
		}

	for(l=0; l<9; l++) // iterate over cols -> reduce boxes
		for(i=0; i<3; i++) {
			int j,k=0;
			for(j=0; j<9; j++)
				if(j%3 != l%3)
					non_intersection_indices[k++]=j; // box indices

			if(simplify_intersection_struc(box[3*i + l/3], col[l], i*3, non_intersection_indices)) {
				printf("simplify_intersection_reduce_box(box%d,col%d)\n", 3*i + l/3+1, l+1);
				return 1;
			}
		}

	for(l=0; l<9; l++) // iterate over boxes -> reduce rows
		for(i=0; i<3; i++) {
			int j,k=0;
			for(j=0; j<9; j++)
				if(j/3 != l%3)
					non_intersection_indices[k++]=j; // row indices

			if(simplify_intersection_struc(row[(l/3)*3+i], box[l], i*3, non_intersection_indices)) {
				printf("simplify_intersection_reduce_row(row%d,box%d)\n", (l/3)*3+i+1, l+1);
				return 1;
			}
		}


	return 0;
}

// offset is the first index of the line in the intersection
int simplify_intersection_struc(int* struc1[9], int* struc2[9], int struc2_offset, int non_intersection_struc1_indices[6]) {
	int progress = 0;
	// first identify fixed values of the line
	int fixed = get_fixed_mask(struc2);
	// then identify values which occur in the intersection
	int m = *struc2[struc2_offset] | *struc2[struc2_offset+1] | *struc2[struc2_offset+2];
	// now remove all fixed values of the line from this set
	m &= ~fixed;
	// then remove from that set all values which occur outside the intersection in that line
	int i;
	for(i=0; i<9; i++)
		if(i<struc2_offset || i > struc2_offset+2)
			m &= ~*struc2[i];

	// when there are no such values, terminate
	if(!m)
		return 0;
	
	// finally check whether such values exist in the box-cells which aren't part of the intersection and remove them
	for(i=0; i<6; i++)
		if(*struc1[non_intersection_struc1_indices[i]] & m) {
			*struc1[non_intersection_struc1_indices[i]] &= ~m;
			progress = 1;
		}

	return progress;
}

// === MAIN FUNCTION ===================================================================================

int main(int argc, char** args) {
	if(argc != 2 || initialize(args[1])) {
		printf("Malformed or missing input!\nExemplary usage: sudoku 7.4.95...9.6.8...73.1.76.9.5...1.84613865472946.8......1.5..97.84.76.23..........\n\n");
		return -1;
	}

	while(!is_solved()) {
//		print_sudoku();

		if(simplify_nakedsingle())
			continue;

		if(simplify_hiddensingle())
			continue;

		if(simplify_nakedpair())
			continue;

		if(simplify_nakedtriple())
			continue;

		if(simplify_hiddenpair())
			continue;

		if(simplify_hiddentriple())
			continue;

		if(simplify_intersection())
			continue;

		printf("THIS SUDOKU IS TOO HARD TO SOLVE FOR ME!\n");
		break;
	}

	print_sudoku();

	return 0;
}
