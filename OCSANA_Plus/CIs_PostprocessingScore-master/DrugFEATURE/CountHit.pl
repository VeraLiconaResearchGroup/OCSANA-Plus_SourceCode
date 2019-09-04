#!/usr/bin/perl
use List::MoreUtils qw(uniq);
# PID step is skipped in this script.  It does not affect the druggability scores significantly.
# scripts for including PID filter is available upon request.
# hitrate is defined as #non-redundant (drug) hit normlaized by the number of uE. 
###########################################################################################################
if ($#ARGV != 2)
        {print "usage: CountHit.pl QueryPointList SiteComparisonResult ScoreCutoff (-2) \n"; exit;}
my $target_list=$ARGV[0];
my $resultFile=$ARGV[1];
my $scoreCutoff=$ARGV[2];
###########################################################################################################
open(MIN, $target_list)|| die "cannot open";my @lines=<MIN>; close MIN;
foreach $lines(@lines)
	{
	chomp $lines;
	print $lines."\t";
	my @name=split(/\t/,$lines);
	$name[0]=~s/\s//; $name[1]=~s/^\s//;
	my $PDB=$name[0]."_".$name[1];

	my @drug=`grep $PDB $resultFile`;

	my $hitNumber=0; my @hit=qw(); $homNumber=($#drug+1); 
	foreach $drug(@drug)
		{
		chomp $drug; my @tmp1=split(/\t/, $drug); 
		# microenvironment cutoff -0.15
		if  ($tmp1[-3] <=$scoreCutoff)
#		 # microenvironment cutoff -0.2
#		if  ($tmp1[-1] <=$scoreCutoff)

		{$hitNumber++; push (@hit, $tmp1[1]);}
		}
	my $numberHitLig=getUniqLig(\@hit); 
	my $numberHomLig=getUniqLig(\@drug);

	print $hitNumber."\t".$numberHitLig."\t";
	my $rate=$numberHitLig/$name[5];
	printf "%7.2f\n", $rate;
	}
###########################################################################################################
sub getUniqLig
{
my($ref)=@_[0];
my @arr=@$ref;
my @all=qw();
foreach $arr(@arr)
	{chomp $arr; my $lig=substr($arr, -3,3); push (@all, $lig);}
my @uniqLig=uniq(@all);
return $#uniqLig+1;
}	
